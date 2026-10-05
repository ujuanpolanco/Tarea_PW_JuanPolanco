
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Listener que lleva el registro de TODAS las sesiones activas del servidor.
 *
 * Por qué hace falta: session.invalidate() solo cierra la sesión del usuario que
 * hace la petición. La API de servlets no ofrece una forma de listar las demás
 * sesiones, así que las guardamos nosotros cada vez que Tomcat crea una y las
 * quitamos cuando se destruye (por invalidate() o por timeout).
 *
 * Tomcat registra este listener solo, gracias a @WebListener.
 */
@WebListener
public class SesionesActivasListener implements HttpSessionListener {

	// Conjunto seguro para hilos: varias peticiones pueden crear/destruir
	// sesiones al mismo tiempo. ConcurrentHashMap.newKeySet() es un Set respaldado
	// por un ConcurrentHashMap.
	private static final Set<HttpSession> SESIONES = ConcurrentHashMap.newKeySet();

	/** Tomcat llama a este método cuando se crea una sesión nueva. */
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		SESIONES.add(se.getSession());
	}

	/** Tomcat llama a este método cuando una sesión se invalida o expira. */
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		SESIONES.remove(se.getSession());
	}

	/** Cantidad de sesiones activas que el listener tiene registradas. */
	public static int cantidad() {
		return SESIONES.size();
	}

	/**
	 * Invalida todas las sesiones registradas y devuelve cuántas se invalidaron.
	 */
	public static int invalidarTodas() {
		// Se recorre una COPIA del conjunto: cada invalidate() dispara
		// sessionDestroyed(), que quita la sesión de SESIONES. Si recorriéramos el
		// conjunto original mientras se modifica, el recorrido sería inconsistente.
		List<HttpSession> copia = new ArrayList<>(SESIONES);
		int invalidadas = 0;
		for (HttpSession sesion : copia) {
			try {
				sesion.invalidate();
				invalidadas++;
			} catch (IllegalStateException e) {
				// La sesión ya estaba invalidada (por timeout, por ejemplo): no se cuenta.
			}
		}
		return invalidadas;
	}
}
