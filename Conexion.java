package conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Gestor de conexión a la base de datos.
 *
 * Implementa el patrón Singleton para garantizar una única instancia de
 * conexión compartida en toda la aplicación, optimizando el uso de recursos y
 * evitando múltiples aperturas de conexión.
 *
 * @author Grupo04
 */
public class Conexion {

    private static Conexion instancia;
    private Connection conexion;

    private static final String URL
            = "jdbc:mysql://localhost:3306/clinica_san_gabriel?useSSL=false&serverTimezone=America/Lima&allowPublicKeyRetrieval=true";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "fernanpro18Gg";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    /**
     * Constructor privado.
     *
     * Impide la creación de instancias externas, forzando el uso del método
     * getInstancia() para acceder a la conexión.
     */
    private Conexion() {
        try {
            Class.forName(DRIVER);
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                    "No se encontró el driver JDBC de MySQL. Verifique que el "
                    + "conector (mysql-connector-j) esté agregado al proyecto.", e);
        } catch (SQLException e) {
            throw new RuntimeException(
                    "No se pudo establecer conexión con la base de datos. "
                    + "Verifique que el servidor MySQL esté activo y los datos "
                    + "de acceso sean correctos.", e);
        }
    }

    /**
     * Punto de acceso global a la instancia única.
     *
     * El uso de synchronized previene problemas de concurrencia al crear la
     * instancia en entornos con múltiples hilos.
     *
     * @return Instancia única de Conexion
     */
    public static synchronized Conexion getInstancia() {
        if (instancia == null) {
            instancia = new Conexion();
        }
        return instancia;
    }

    /**
     * Devuelve la conexión activa a la base de datos.
     *
     * Si la conexión se encuentra cerrada o es nula, se restablece
     * automáticamente antes de ser devuelta.
     *
     * @return Conexión activa a la base de datos
     * @throws RuntimeException Si ocurre un error al restablecer la conexión
     */
    public Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al verificar el estado de la conexión.", e);
        }
        return conexion;
    }

    /**
     * Cierra la conexión activa a la base de datos.
     *
     * Libera los recursos asociados a la conexión actual.
     */
    public void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexión: " + e.getMessage());
        }
    }
}
