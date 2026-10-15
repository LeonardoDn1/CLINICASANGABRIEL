package logica;

import entidades.*;
import datos.*;
import java.util.*;
import static javax.swing.JOptionPane.showMessageDialog;
import org.mindrot.jbcrypt.BCrypt;

/**
 * Capa de lógica de negocio para la entidad Usuario.
 *
 * Gestiona la autenticación, registro, consulta y control de acceso de los
 * usuarios del sistema, incluyendo encriptación de contraseñas y validaciones
 * de seguridad.
 *
 * @author Grupo04
 */
public class BLLUsuario {

    /**
     * Autentica a un usuario verificando sus credenciales.
     *
     * @param username Nombre de usuario
     * @param passwordPlana Contraseña en texto plano
     * @return Objeto Usuario autenticado, o null si falla
     */
    public Usuario autenticarUsuario(String username, String passwordPlana) {
        if (username != null && !username.trim().isEmpty()
                && passwordPlana != null && !passwordPlana.trim().isEmpty()) {

            DAL_Usuario dal = new DAL_Usuario();
            Usuario usuario = dal.autenticar(username, passwordPlana);

            if (usuario != null) {
                return usuario;
            } else {
                showMessageDialog(null, "Usuario o contraseña incorrectos", "Error de Acceso", 0);
                return null;
            }
        } else {
            showMessageDialog(null, "Datos de acceso no válidos", "Error", 0);
            return null;
        }
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param username Nombre de usuario único
     * @param passwordPlana Contraseña en texto plano (será encriptada)
     * @param idRol Identificador del rol asignado
     * @param nombres Nombres del usuario
     * @param apellidos Apellidos del usuario
     * @param email Correo electrónico
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int insertarUsuario(String username, String passwordPlana, int idRol,
            String nombres, String apellidos, String email) {

        // Validación de campos obligatorios
        if (username == null || username.trim().isEmpty()
                || nombres == null || nombres.trim().isEmpty()
                || apellidos == null || apellidos.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || idRol <= 0) {

            showMessageDialog(null, "Todos los campos obligatorios deben estar completos (incluyendo el Email).", "Error", 0);
            return 2;
        }

        // Validación de formato de email
        if (!validarEmail(email)) {
            showMessageDialog(null, "El correo electrónico debe tener un formato válido con '@' (ejemplo: usuario@correo.com).", "Error de Correo", 0);
            return 2;
        }

        // Validación de complejidad de contraseña
        if (passwordPlana == null || !validarPassword(passwordPlana)) {
            showMessageDialog(null,
                    "La contraseña no cumple los requisitos de seguridad:\n"
                    + "- Mínimo 8 caracteres\n"
                    + "- Al menos una letra mayúscula\n"
                    + "- Al menos una letra minúscula\n"
                    + "- Al menos un número\n"
                    + "- Al menos un carácter especial (!@#$%^&*, etc.)",
                    "Contraseña Débil", 0);
            return 2;
        }

        // Encriptación automática con BCrypt
        String hashGenerado = BCrypt.hashpw(passwordPlana, BCrypt.gensalt());

        Usuario usuario = new Usuario(0, username, hashGenerado, idRol, nombres, apellidos, email, "ACTIVO");
        DAL_Usuario dal = new DAL_Usuario();

        if (dal.insertar(usuario)) {
            showMessageDialog(null, "Usuario registrado exitosamente con clave encriptada.", "Éxito", 1);
            return 0;
        } else {
            showMessageDialog(null, "Error al registrar usuario en la base de datos. Verifique si el username ya existe.", "Error", 0);
            return 1;
        }
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param idUsuario Identificador del usuario
     * @return Objeto Usuario encontrado, o null si no existe
     */
    public Usuario buscarUsuario(int idUsuario) {
        if (idUsuario > 0) {
            DAL_Usuario dal = new DAL_Usuario();
            return dal.buscarPorId(idUsuario);
        } else {
            showMessageDialog(null, "Código de usuario no válido", "Error", 0);
            return null;
        }
    }

    /**
     * Lista todos los usuarios registrados.
     *
     * @return Lista de objetos Usuario
     */
    public ArrayList<Usuario> listarUsuarios() {
        DAL_Usuario dal = new DAL_Usuario();
        return new ArrayList<>(dal.listarTodos());
    }

    /**
     * Actualiza la información de un usuario existente.
     *
     * @param idUsuario Identificador del usuario
     * @param idRol Nuevo identificador del rol
     * @param nombres Nuevos nombres del usuario
     * @param apellidos Nuevos apellidos del usuario
     * @param email Nuevo correo electrónico
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si los datos son
     * inválidos
     */
    public int actualizarUsuario(int idUsuario, int idRol, String nombres, String apellidos, String email) {
        if (idUsuario > 0 && idRol > 0
                && nombres != null && !nombres.trim().isEmpty()
                && apellidos != null && !apellidos.trim().isEmpty()) {

            Usuario usuario = new Usuario(idUsuario, "", "", idRol, nombres, apellidos, email, "ACTIVO");
            DAL_Usuario dal = new DAL_Usuario();

            if (dal.actualizar(usuario)) {
                showMessageDialog(null, "Usuario actualizado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "Error al actualizar usuario", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Datos no válidos", "Error", 0);
            return 2;
        }
    }

    /**
     * Bloquea un usuario (cambia estado a BLOQUEADO).
     *
     * @param idUsuario Identificador del usuario
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int bloquearUsuario(int idUsuario) {
        if (idUsuario > 0) {
            DAL_Usuario dal = new DAL_Usuario();
            if (dal.eliminarLogico(idUsuario)) {
                showMessageDialog(null, "Usuario bloqueado", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo bloquear usuario", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no válido", "Error", 0);
            return 2;
        }
    }

    /**
     * Desbloquea un usuario (cambia estado a ACTIVO).
     *
     * @param idUsuario Identificador del usuario
     * @return 0 si fue exitoso, 1 si ocurrió un error, 2 si el identificador es
     * inválido
     */
    public int desbloquearUsuario(int idUsuario) {
        if (idUsuario > 0) {
            DAL_Usuario dal = new DAL_Usuario();
            if (dal.activarUsuario(idUsuario)) {
                showMessageDialog(null, "Usuario desbloqueado exitosamente", "Resultado", 1);
                return 0;
            } else {
                showMessageDialog(null, "No se pudo desbloquear al usuario", "Error", 0);
                return 1;
            }
        } else {
            showMessageDialog(null, "Código no válido", "Error", 0);
            return 2;
        }
    }

    /**
     * Valida el formato del correo electrónico.
     *
     * @param email Correo electrónico a validar
     * @return true si el formato es válido, false en caso contrario
     */
    private boolean validarEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(regex);
    }

    /**
     * Valida la complejidad de la contraseña.
     *
     * @param password Contraseña a validar
     * @return true si cumple con los requisitos, false en caso contrario
     */
    private boolean validarPassword(String password) {
        if (password.length() < 8) {
            return false;
        }

        boolean mayuscula = password.matches(".*[A-Z].*");
        boolean minuscula = password.matches(".*[a-z].*");
        boolean numero = password.matches(".*[0-9].*");
        boolean especial = password.matches(".*[^a-zA-Z0-9].*");

        return mayuscula && minuscula && numero && especial;
    }
}
