
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Servlet1
 *
 * GET: muestra el formulario. POST: valida los datos en el servidor y, si todo
 * está bien, guarda 5 cookies (nombre, apellido, edad, ciudad y programa).
 */
@WebServlet("/Servlet1")
public class Servlet1 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// El nombre del campo del formulario es también el nombre de la cookie.
	private static final String[] CAMPOS = { "nombre", "apellido", "edad", "ciudad", "programa" };
	private static final String[] ETIQUETAS = { "Nombre", "Apellido", "Edad", "Ciudad", "Programa académico" };

	private static final int EDAD_MIN = 1;
	private static final int EDAD_MAX = 120;

	// Duración de las cookies: 7 días, en segundos.
	private static final int DURACION_COOKIE = 7 * 24 * 60 * 60;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Servlet1() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		// Formulario vacío: sin valores previos y sin errores.
		mostrarFormulario(out, new String[CAMPOS.length], new ArrayList<>());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Debe ir ANTES de leer cualquier parámetro, si no las tildes y la ñ llegan mal.
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String[] valores = new String[CAMPOS.length];
		List<String> errores = validar(request, valores);

		if (!errores.isEmpty()) {
			// Hay errores: se vuelve a mostrar el formulario con los mensajes y con lo
			// que el usuario ya había escrito.
			mostrarFormulario(out, valores, errores);
			return;
		}

		// Todo es válido: se crea una cookie por cada campo.
		for (int i = 0; i < CAMPOS.length; i++) {
			response.addCookie(crearCookie(CAMPOS[i], valores[i]));
		}

		Html.abrir(out, "Cookies guardadas");
		out.print("<p>Se guardaron " + CAMPOS.length + " cookies:</p><ul>");
		for (int i = 0; i < CAMPOS.length; i++) {
			out.print("<li>" + ETIQUETAS[i] + ": " + Html.escapar(valores[i]) + "</li>");
		}
		out.print("</ul>");

		// Botón para ir a ver las cookies (igual que en el ejemplo original)
		out.print("<form action='Servlet2' method='post'>");
		out.print("<input type='submit' value='Ver cookies'>");
		out.print("</form>");
		Html.cerrar(out);
	}

	/**
	 * Lee y valida los campos del formulario. Deja en 'valores' lo que escribió el
	 * usuario (ya sin espacios al inicio/fin) y devuelve la lista de errores
	 * (vacía si todo es válido).
	 */
	private List<String> validar(HttpServletRequest request, String[] valores) {
		List<String> errores = new ArrayList<>();

		for (int i = 0; i < CAMPOS.length; i++) {
			String valor = request.getParameter(CAMPOS[i]);
			valores[i] = (valor == null) ? "" : valor.trim();

			// Regla 1: campo obligatorio (no vacío ni solo espacios).
			if (valores[i].isEmpty()) {
				errores.add("El campo " + ETIQUETAS[i] + " es obligatorio.");
			}
		}

		// Regla 2: la edad debe ser un entero entre EDAD_MIN y EDAD_MAX. Solo se revisa
		// si no está vacía, para no repetir el mensaje de obligatorio.
		String edad = valores[2];
		if (!edad.isEmpty()) {
			try {
				int numero = Integer.parseInt(edad);
				if (numero < EDAD_MIN || numero > EDAD_MAX) {
					errores.add("La edad debe estar entre " + EDAD_MIN + " y " + EDAD_MAX + ".");
				}
			} catch (NumberFormatException e) {
				errores.add("La edad debe ser un número entero.");
			}
		}

		return errores;
	}

	/**
	 * Crea una cookie con el valor codificado en URL: así se pueden guardar
	 * espacios, tildes y ñ (Tomcat no permite esos caracteres crudos en una cookie).
	 */
	private Cookie crearCookie(String nombre, String valor) throws IOException {
		Cookie ck = new Cookie(nombre, URLEncoder.encode(valor, "UTF-8"));
		ck.setMaxAge(DURACION_COOKIE); // sin esto la cookie muere al cerrar el navegador
		ck.setPath("/"); // visible para todas las rutas de la aplicación
		return ck;
	}

	/** Imprime el formulario, con la lista de errores (si hay) y los valores previos. */
	private void mostrarFormulario(PrintWriter out, String[] valores, List<String> errores) {
		Html.abrir(out, "Formulario de cookies");

		if (!errores.isEmpty()) {
			out.print("<ul style='color:red'>");
			for (String error : errores) {
				out.print("<li>" + Html.escapar(error) + "</li>");
			}
			out.print("</ul>");
		}

		out.print("<form action='Servlet1' method='post'>");
		for (int i = 0; i < CAMPOS.length; i++) {
			String previo = (valores[i] == null) ? "" : valores[i];
			// La edad es type='text' a propósito, para que la validación la haga el servidor.
			out.print(ETIQUETAS[i] + ": <input type='text' name='" + CAMPOS[i] + "' value='" + Html.escapar(previo)
					+ "'/><br/>");
		}
		out.print("<input type='submit' value='Guardar'/>");
		out.print("</form>");

		Html.cerrar(out);
	}

}
