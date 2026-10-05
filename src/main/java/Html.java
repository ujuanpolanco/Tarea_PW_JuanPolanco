
import java.io.PrintWriter;

/**
 * Utilidades pequeñas para armar las páginas HTML de los servlets y evitar
 * repetir el mismo encabezado/pie en cada uno.
 */
public final class Html {

	private Html() {
	}

	/**
	 * Escapa los caracteres especiales de HTML. Se usa siempre que se imprime un
	 * valor que escribió el usuario (parámetros o cookies), para que no se
	 * interprete como código HTML (XSS) y para conservar bien comillas y símbolos.
	 */
	public static String escapar(String s) {
		if (s == null) {
			return "";
		}
		return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&#39;");
	}

	/** Abre el documento HTML (UTF-8 para que se vean tildes y ñ). */
	public static void abrir(PrintWriter out, String titulo) {
		out.print("<!DOCTYPE html>");
		out.print("<html><head><meta charset='UTF-8'><title>" + escapar(titulo) + "</title></head><body>");
		out.print("<h2>" + escapar(titulo) + "</h2>");
	}

	/** Cierra el documento e incluye el enlace al menú principal. */
	public static void cerrar(PrintWriter out) {
		out.print("<hr/><a href='index.html'>Volver al menú</a>");
		out.print("</body></html>");
	}
}
