/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package datos;

import conexion.Conexion;
import entidades.Paciente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de acceso a datos para la entidad Paciente.
 *
 * Gestiona las operaciones CRUD y consultas específicas relacionadas con los
 * pacientes registrados en la clínica, utilizando procedimientos almacenados
 * para las operaciones principales.
 *
 * @author Grupo04
 */
public class DAL_Paciente implements DAL_iCRUD<Paciente> {

    /**
     * Inserta un nuevo paciente (implementación de la interfaz).
     *
     * @param p Objeto Paciente con los datos a registrar
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean insertar(Paciente p) {
        return insertarPaciente(p) > 0;
    }

    /**
     * Registra un paciente y retorna el ID generado.
     *
     * @param p Objeto Paciente con los datos a registrar
     * @return ID del paciente registrado, o 0 si ocurrió un error
     */
    public int insertarPaciente(Paciente p) {
        String sql = "{call SP_RegistrarPacienteConApoderado(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, p.getNumeroHistoriaClinica());
            ps.setString(2, p.getDni());
            ps.setString(3, p.getNombres());
            ps.setString(4, p.getApellidos());

            if (p.getFechaNacimiento() != null) {
                ps.setDate(5, new java.sql.Date(p.getFechaNacimiento().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, p.getSexo());
            ps.setString(7, p.getTelefono());
            ps.setString(8, p.getDireccion());
            ps.setString(9, p.getEstado() != null ? p.getEstado() : "ACTIVO");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Paciente.insertarPaciente: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Registra un paciente utilizando una conexión existente (para
     * transacciones).
     *
     * @param p Objeto Paciente con los datos a registrar
     * @param con Conexión activa a la base de datos
     * @return ID del paciente registrado, o 0 si ocurrió un error
     */
    public int insertarPaciente(Paciente p, Connection con) {
        String sql = "{call SP_RegistrarPacienteConApoderado(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, p.getNumeroHistoriaClinica());
            ps.setString(2, p.getDni());
            ps.setString(3, p.getNombres());
            ps.setString(4, p.getApellidos());

            if (p.getFechaNacimiento() != null) {
                ps.setDate(5, new java.sql.Date(p.getFechaNacimiento().getTime()));
            } else {
                ps.setNull(5, Types.DATE);
            }

            ps.setString(6, p.getSexo());
            ps.setString(7, p.getTelefono());
            ps.setString(8, p.getDireccion());
            ps.setString(9, p.getEstado() != null ? p.getEstado() : "ACTIVO");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Paciente.insertarPaciente transacción: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Actualiza los datos de un paciente existente.
     *
     * @param p Objeto Paciente con los datos actualizados
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean actualizar(Paciente p) {
        String sql = "{call sp_actualizar_paciente(?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setString(1, p.getNombres());
            ps.setString(2, p.getApellidos());

            if (p.getFechaNacimiento() != null) {
                ps.setDate(3, new java.sql.Date(p.getFechaNacimiento().getTime()));
            } else {
                ps.setNull(3, Types.DATE);
            }

            ps.setString(4, p.getSexo());
            ps.setString(5, p.getTelefono());
            ps.setString(6, p.getDireccion());
            ps.setString(7, p.getEstado());
            ps.setInt(8, p.getIdPaciente());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Paciente.actualizar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina lógicamente un paciente (cambia estado a INACTIVO).
     *
     * @param idPaciente Identificador del paciente
     * @return true si la operación fue exitosa, false en caso contrario
     */
    @Override
    public boolean eliminarLogico(int idPaciente) {
        String sql = "{call sp_eliminar_paciente(?)}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            DAL_Auditoria.establecerUsuarioSesion(con, SesionUsuario.getIdUsuarioLogueado());

            ps.setInt(1, idPaciente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error en DAL_Paciente.eliminarLogico: " + e.getMessage());
            return false;
        }
    }

    /**
     * Busca un paciente por su identificador mediante procedimiento almacenado.
     *
     * @param id Identificador del paciente
     * @return Objeto Paciente encontrado, o null si no existe
     */
    @Override
    public Paciente buscarPorId(int id) {
        String sql = "{call sp_buscar_paciente_por_id(?)}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Paciente.buscarPorId: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca un paciente por su número de DNI.
     *
     * @param dni Documento nacional de identidad del paciente
     * @return Objeto Paciente encontrado, o null si no existe
     */
    public Paciente buscarPorDni(String dni) {
        String sql = "{call sp_buscar_paciente_por_dni(?)}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setString(1, dni);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Paciente.buscarPorDni: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca un paciente por su número de historia clínica.
     *
     * @param hc Número de historia clínica del paciente
     * @return Objeto Paciente encontrado, o null si no existe
     */
    public Paciente buscarPorHistoriaClinica(String hc) {
        String sql = "{call sp_buscar_paciente_por_historia_clinica(?)}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            ps.setString(1, hc);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Paciente.buscarPorHistoriaClinica: " + e.getMessage());
        }
        return null;
    }

    /**
     * Busca pacientes por coincidencia en nombres o apellidos.
     *
     * @param criterio Texto a buscar en nombres o apellidos
     * @return Lista de pacientes que coinciden con el criterio
     */
    public List<Paciente> buscarPorNombreOApellido(String criterio) {
        List<Paciente> lista = new ArrayList<>();
        String sql = "{call sp_buscar_paciente_por_nombre_apellido(?, ?)}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql)) {

            String filtro = "%" + criterio + "%";
            ps.setString(1, filtro);
            ps.setString(2, filtro);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearPaciente(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Paciente.buscarPorNombreOApellido: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Lista todos los pacientes registrados.
     *
     * @return Lista de objetos Paciente
     */
    @Override
    public List<Paciente> listarTodos() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "{call sp_listar_pacientes()}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPaciente(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error en DAL_Paciente.listarTodos: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Genera un nuevo número de historia clínica.
     *
     * @return Nuevo número de historia clínica con formato HC-XXXXX
     */
    public String generarNuevaHistoriaClinica() {
        String sql = "{call sp_generar_nueva_historia_clinica()}";

        try (Connection con = Conexion.getInstancia().getConexion(); CallableStatement ps = con.prepareCall(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                int siguiente = rs.getInt("ultimo") + 1;
                return String.format("HC-%05d", siguiente);
            }
        } catch (SQLException e) {
            System.err.println("Error al generar historia clínica: " + e.getMessage());
        }
        return "HC-00001";
    }

    /**
     * Convierte un ResultSet en un objeto Paciente.
     *
     * @param rs ResultSet con los datos del paciente
     * @return Objeto Paciente mapeado
     * @throws SQLException Si ocurre un error al acceder a los datos
     */
    private Paciente mapearPaciente(ResultSet rs) throws SQLException {
        return new Paciente.Builder(
                rs.getString("dni"),
                rs.getString("nombres"),
                rs.getString("apellidos"),
                rs.getDate("fecha_nacimiento"),
                rs.getString("sexo"),
                rs.getString("telefono"),
                rs.getString("direccion")
        )
                .idPaciente(rs.getInt("id_paciente"))
                .numeroHistoriaClinica(rs.getString("numero_historia_clinica"))
                .estado(rs.getString("estado"))
                .fechaRegistro(rs.getTimestamp("fecha_registro"))
                .build();
    }
}
