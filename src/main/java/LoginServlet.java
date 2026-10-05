
import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Servlet implementation class LoginServlet
 *
 * GET: si ya hay sesión iniciada muestra la bienvenida; si no, el formulario de
 * login. POST: valida usuario y contraseña y, si son correctos, crea la sesión.
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Credenciales fijas, solo para la tarea (en un sistema real irían en una base
	// de datos y la contraseña se guardaría con hash).
	private static final String USUARIO = "admin";
	private static final String CLAVE = "1234";

	// Nombre del atributo de sesión que indica que el usuario ya inició sesión.
	// Lo usa también InvalidarSesionesServlet.
	static final String ATRIBUTO_USUARIO = "usuario";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
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

		// getSession(false): devuelve la sesión si existe, pero NO crea una nueva.
		// Así quien solo mira el formulario no genera sesiones de más.
		HttpSession session = request.getSession(false);
		if (session != null && session.getAttribute(ATRIBUTO_USUARIO) != null) {
			mostrarBienvenida(out, (String) session.getAttribute(ATRIBUTO_USUARIO));
		} else {
			mostrarFormulario(out, "", null);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter out = response.getWriter();

		String usuario = request.getParameter("usuario");
		String clave = request.getParameter("clave");
		usuario = (usuario == null) ? "" : usuario.trim();
		clave = (clave == null) ? "" : clave;

		// Validación: campos obligatorios.
		if (usuario.isEmpty() || clave.isEmpty()) {
			mostrarFormulario(out, usuario, "Usuario y contraseña son obligatorios.");
			return;
		}

		// Autenticación contra las credenciales fijas.
		if (!USUARIO.equals(usuario) || !CLAVE.equals(clave)) {
			mostrarFormulario(out, usuario, "Usuario o contraseña incorrectos.");
			return;
		}

		// Login correcto: ahora sí se crea la sesión (Tomcat dispara sessionCreated en
		// el listener) y se guarda el usuario como atributo.
		HttpSession session = request.getSession(true);
		session.setAttribute(ATRIBUTO_USUARIO, usuario);

		// Redirigir evita que al recargar la página se reenvíe el formulario.
		response.sendRedirect("LoginServlet");
	}

	private void mostrarFormulario(PrintWriter out, String usuario, String error) {
		Html.abrir(out, "Iniciar sesión");

		if (error != null) {
			out.print("<p style='color:red'>" + Html.escapar(error) + "</p>");
		}

		out.print("<form action='LoginServlet' method='post'>");
		out.print("Usuario: <input type='text' name='usuario' value='" + Html.escapar(usuario) + "'/><br/>");
		out.print("Contraseña: <input type='password' name='clave'/><br/>");
		out.print("<input type='submit' value='Entrar'/>");
		out.print("</form>");

		Html.cerrar(out);
	}

	private void mostrarBienvenida(PrintWriter out, String usuario) {
		Html.abrir(out, "Sesión iniciada");

		out.print("<p>Bienvenido, " + Html.escapar(usuario) + ".</p>");
		out.print("<p>Sesiones activas en el servidor: " + SesionesActivasListener.cantidad() + "</p>");

		out.print("<form action='InvalidarSesionesServlet' method='post'>");
		out.print("<input type='submit' value='Invalidar todas las sesiones'/>");
		out.print("</form>");

		Html.cerrar(out);
	}

}
