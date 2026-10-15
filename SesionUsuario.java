package datos;

/**
 * Gestor de la sesión activa del usuario en el sistema.
 *
 * Mantiene el identificador del usuario autenticado durante toda la ejecución
 * de la aplicación, permitiendo su acceso desde cualquier capa del sistema.
 *
 * @author Grupo04
 */
public final class SesionUsuario {

    private static Integer idUsuarioLogueado = null;

    private SesionUsuario() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Establece el usuario actual en sesión.
     *
     * @param idUsuario Identificador del usuario autenticado
     * @throws IllegalArgumentException Si el ID es menor o igual a cero
     */
    public static void setUsuarioLogueado(int idUsuario) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID del usuario debe ser mayor que cero");
        }
        idUsuarioLogueado = idUsuario;
    }

    /**
     * Obtiene el identificador del usuario en sesión.
     *
     * @return ID del usuario autenticado
     * @throws IllegalStateException Si no hay un usuario autenticado
     */
    public static int getIdUsuarioLogueado() {
        if (idUsuarioLogueado == null) {
            throw new IllegalStateException("No existe un usuario autenticado");
        }
        return idUsuarioLogueado;
    }

    /**
     * Verifica si hay una sesión activa.
     *
     * @return true si hay un usuario autenticado, false en caso contrario
     */
    public static boolean haySesionActiva() {
        return idUsuarioLogueado != null;
    }

    /**
     * Cierra la sesión actual.
     */
    public static void cerrarSesion() {
        idUsuarioLogueado = null;
    }
}
