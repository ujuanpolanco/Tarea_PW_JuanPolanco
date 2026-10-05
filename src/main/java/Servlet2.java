
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Servlet2
 *
 * Lee todas las cookies que envía el navegador y las muestra en una tabla.
 */
@WebServlet("/Servlet2")
public class Servlet2 extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Servlet2() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		Html.abrir(out, "Cookies recibidas");

		Cookie ck[] = request.getCookies();

		// getCookies() devuelve null (no un arreglo vacío) si no llegó ninguna cookie.
		if (ck == null || ck.length == 0) {
			out.print("<p>No hay cookies. Primero llena el <a href='Servlet1'>formulario</a>.</p>");
		} else {
			out.print("<table border='1' cellpadding='4'>");
			out.print("<tr><th>Nombre</th><th>Valor</th></tr>");
			for (Cookie c : ck) {
				// El valor se guardó con URLEncoder, así que se decodifica al leerlo.
				String valor = URLDecoder.decode(c.getValue(), "UTF-8");
				out.print("<tr><td>" + Html.escapar(c.getName()) + "</td><td>" + Html.escapar(valor) + "</td></tr>");
			}
			out.print("</table>");
		}

		Html.cerrar(out);
	}

}
