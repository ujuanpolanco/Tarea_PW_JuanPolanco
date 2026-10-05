
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class InvalidarSesionesServlet
 *
 * Invalida TODAS las sesiones activas del servidor (no solo la del usuario que
 * hace la petición). Solo funciona si el usuario ya inició sesión.
 */
@WebServlet("/InvalidarSesionesServlet")
public class InvalidarSesionesServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InvalidarSesionesServlet() {
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
		// Verificación de login: sin sesión, o con sesión sin el atributo "usuario",
		// se manda al login. getSession(false) no crea una sesión nueva.
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute(LoginServlet.ATRIBUTO_USUARIO) == null) {
			response.sendRedirect("LoginServlet");
			return;
		}

		// Se guarda el nombre antes de invalidar: después de invalidate() la sesión ya
		// no se puede leer.
		String usuario = (String) session.getAttribute(LoginServlet.ATRIBUTO_USUARIO);

		// Invalida todas las sesiones registradas por el listener, incluida esta.
		int invalidadas = SesionesActivasListener.invalidarTodas();

		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		Html.abrir(out, "Sesiones invalidadas");
		out.print("<p>" + Html.escapar(usuario) + ", se invalidaron " + invalidadas
				+ " sesión(es), incluida la tuya.</p>");
		out.print("<a href='LoginServlet'>Iniciar sesión de nuevo</a>");
		Html.cerrar(out);
	}

}
