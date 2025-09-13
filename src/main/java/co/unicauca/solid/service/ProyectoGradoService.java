package co.unicauca.solid.service;

import co.unicauca.solid.access.IProyectoGradoRepository;
import co.unicauca.solid.access.ProyectoGradoRepository;
import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.solid.domain.User;
import co.unicauca.solid.domain.enums.EstadoEnum;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import co.unicauca.utilities.validators.ValidationUtil;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de proyectos de grado
 * Implementa toda la lógica de negocio según los requisitos funcionales
 */
public class ProyectoGradoService {

    private static final Logger LOGGER = Logger.getLogger(ProyectoGradoService.class.getName());

    private final IProyectoGradoRepository proyectoRepository;
    private final UserService userService;

    public ProyectoGradoService(IProyectoGradoRepository proyectoRepository, UserService userService) {
        this.proyectoRepository = proyectoRepository;
        this.userService = userService;
    }

    // ========== MÉTODOS PRINCIPALES DE CRUD ==========
    /**
     * Crea un nuevo proyecto de grado con validaciones completas
     */
    public int crearProyecto(ProyectoGrado proyecto) throws InvalidUserDataException, UserNotFoundException {
        validarProyecto(proyecto);

        // Establecer valores por defecto
        if (proyecto.getFechaCreacion() == null) {
            proyecto.setFechaCreacion(LocalDateTime.now());
        }
        if (proyecto.getFechaUltimaActualizacion() == null) {
            proyecto.setFechaUltimaActualizacion(LocalDateTime.now());
        }
        if (proyecto.getNumeroIntento() == 0) {
            proyecto.setNumeroIntento(1);
        }
        if (proyecto.getEstadoActual() == null) {
            proyecto.setEstadoActual(EstadoEnum.EN_PRIMERA_EVALUACION_FORMATO_A.getValor());
        }
        if (proyecto.getRechazadoDefinitivamente() == 0) {
            proyecto.setRechazadoDefinitivamente('N');
        }

        int idGenerado = proyectoRepository.insertarProyecto(proyecto);

        if (idGenerado > 0) {
            LOGGER.info("Proyecto creado exitosamente con ID: " + idGenerado);
            // Simular envío de email de notificación
            simularEnvioEmailCreacion(proyecto);
        }

        return idGenerado;
    }

    /**
     * Obtiene un proyecto por su ID
     */
    public ProyectoGrado obtenerProyecto(int idProyecto) throws UserNotFoundException {
        ProyectoGrado proyecto = proyectoRepository.obtenerProyectoPorId(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }
        return proyecto;
    }

    /**
     * Obtiene todos los proyectos de un estudiante
     */
    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String estudianteEmail)
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarEmail(estudianteEmail, "email del estudiante");

        // Verificar que el estudiante existe
        userService.findByEmail(estudianteEmail);

        List<ProyectoGrado> proyectos = proyectoRepository.obtenerProyectosPorEstudiante(estudianteEmail);
        if (proyectos.isEmpty()) {
            throw new UserNotFoundException("No se encontraron proyectos para el estudiante: " + estudianteEmail);
        }
        return proyectos;
    }

    /**
     * Obtiene todos los proyectos de un director
     */
    public List<ProyectoGrado> obtenerProyectosPorDirector(String directorEmail)
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarEmail(directorEmail, "email del director");

        // Verificar que el director existe
        userService.findByEmail(directorEmail);

        List<ProyectoGrado> proyectos = proyectoRepository.obtenerProyectosPorDirector(directorEmail);
        if (proyectos.isEmpty()) {
            throw new UserNotFoundException("No se encontraron proyectos para el director: " + directorEmail);
        }
        return proyectos;
    }

    /**
     * Obtiene proyectos pendientes de evaluación (para coordinadores)
     */
    public List<ProyectoGrado> obtenerProyectosPendientesEvaluacion() throws UserNotFoundException {
        ProyectoGradoRepository repo = (ProyectoGradoRepository) proyectoRepository;
        List<ProyectoGrado> proyectos = repo.obtenerProyectosPendientesEvaluacion();

        if (proyectos.isEmpty()) {
            throw new UserNotFoundException("No hay proyectos pendientes de evaluación");
        }
        return proyectos;
    }

    /**
     * Obtiene todos los proyectos del sistema
     */
    public List<ProyectoGrado> obtenerTodosProyectos() throws UserNotFoundException {
        List<ProyectoGrado> proyectos = proyectoRepository.obtenerTodosProyectos();
        if (proyectos.isEmpty()) {
            throw new UserNotFoundException("No se encontraron proyectos en el sistema");
        }
        return proyectos;
    }

    /**
     * Actualiza un proyecto existente
     */
    public boolean actualizarProyecto(ProyectoGrado proyecto) throws InvalidUserDataException, UserNotFoundException {
        validarProyecto(proyecto);

        // Verificar que el proyecto existe
        ProyectoGrado proyectoExistente = obtenerProyecto(proyecto.getIdProyecto());
        if (proyectoExistente == null) {
            throw new UserNotFoundException("Proyecto con ID " + proyecto.getIdProyecto() + " no encontrado");
        }

        // Actualizar fecha de última modificación
        proyecto.setFechaUltimaActualizacion(LocalDateTime.now());

        return proyectoRepository.actualizarProyecto(proyecto);
    }

    /**
     * Elimina un proyecto
     */
    public boolean eliminarProyecto(int idProyecto) throws UserNotFoundException {
        // Verificar que el proyecto existe
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }

        return proyectoRepository.eliminarProyecto(idProyecto);
    }

    // ========== MÉTODOS ESPECÍFICOS PARA EVALUACIÓN DE FORMATO A ==========
    /**
     * Evalúa un formato A - Método principal para coordinadores Requisito 3:
     * Evaluación del coordinador
     */
    public boolean evaluarFormatoA(int idProyecto, boolean aprobado, String observaciones, String coordinadorEmail)
            throws UserNotFoundException, InvalidUserDataException {

        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");
        ValidationUtil.validarEmail(coordinadorEmail, "email del coordinador");

        // Verificar que el proyecto existe
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);

        // Verificar que el coordinador existe
        userService.findByEmail(coordinadorEmail);

        // Verificar que el proyecto está en un estado que permite evaluación
        if (!puedeSerEvaluado(proyecto.getEstadoActual())) {
            throw new InvalidUserDataException("El proyecto no está en un estado que permita evaluación");
        }

        ProyectoGradoRepository repo = (ProyectoGradoRepository) proyectoRepository;
        boolean resultado = repo.evaluarFormatoA(idProyecto, aprobado, observaciones);

        if (resultado) {
            // Simular envío de email de notificación
            simularEnvioEmailEvaluacion(proyecto, aprobado, observaciones, coordinadorEmail);

            String accion = aprobado ? "aprobado" : "rechazado";
            LOGGER.info("Formato A " + accion + " para proyecto ID: " + idProyecto + " por coordinador: " + coordinadorEmail);
        }

        return resultado;
    }

    /**
     * Procesa un reintento de formato A después de rechazo Requisito 4: Nueva
     * versión tras rechazo
     */
    public boolean procesarReintentoFormatoA(int idProyecto) throws UserNotFoundException, InvalidUserDataException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");

        // Verificar que el proyecto existe
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);

        // Verificar que puede reintentar
        if (!proyecto.puedeReintentar()) {
            throw new InvalidUserDataException("El proyecto ha agotado sus intentos permitidos (máximo 3)");
        }

        // Verificar estado actual
        if (!EstadoEnum.FORMATO_A_RECHAZADO.getValor().equals(proyecto.getEstadoActual())) {
            throw new InvalidUserDataException("Solo se puede reintentar un proyecto con formato A rechazado");
        }

        ProyectoGradoRepository repo = (ProyectoGradoRepository) proyectoRepository;
        boolean resultado = repo.procesarReintentoFormatoA(idProyecto);

        if (resultado) {
            LOGGER.info("Reintento procesado para proyecto ID: " + idProyecto
                    + ", nuevo intento: " + (proyecto.getNumeroIntento() + 1));

            // Simular email de notificación de reintento
            simularEnvioEmailReintento(proyecto);
        }

        return resultado;
    }

    /**
     * Actualiza solo el estado de un proyecto
     */
    public boolean actualizarEstadoProyecto(int idProyecto, String nuevoEstado)
            throws UserNotFoundException, InvalidUserDataException {

        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");

        // Verificar que el proyecto existe
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }

        validarEstado(nuevoEstado);

        boolean resultado = proyectoRepository.actualizarEstadoProyecto(idProyecto, nuevoEstado);

        if (resultado) {
            LOGGER.info("Estado del proyecto " + idProyecto + " actualizado a: " + nuevoEstado);
        }

        return resultado;
    }

    /**
     * Marca un proyecto como rechazado definitivamente
     */
    public boolean marcarRechazoDefinitivo(int idProyecto) throws UserNotFoundException, InvalidUserDataException {
        ValidationUtil.validarPositivo(idProyecto, "ID del proyecto");

        // Verificar que el proyecto existe
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }

        boolean resultado = proyectoRepository.marcarRechazoDefinitivo(idProyecto);

        if (resultado) {
            LOGGER.warning("Proyecto " + idProyecto + " marcado como rechazado definitivamente");
            // Simular email de notificación de rechazo definitivo
            simularEnvioEmailRechazoDefinitivo(proyecto);
        }

        return resultado;
    }

    // ========== MÉTODOS DE CONSULTA Y VALIDACIÓN ==========
    /**
     * Verifica si un proyecto puede reintentar formato A
     */
    public boolean puedeReintentarFormato(int idProyecto) throws UserNotFoundException {
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        return proyecto != null && proyecto.puedeReintentar();
    }

    /**
     * Verifica si un proyecto puede ser evaluado
     */
    public boolean puedeSerEvaluado(String estadoActual) {
        return estadoActual != null && (estadoActual.equals(EstadoEnum.EN_PRIMERA_EVALUACION_FORMATO_A.getValor())
                || estadoActual.equals(EstadoEnum.EN_SEGUNDA_EVALUACION_FORMATO_A.getValor())
                || estadoActual.equals(EstadoEnum.EN_TERCERA_EVALUACION_FORMATO_A.getValor()));
    }

    /**
     * Obtiene el estado legible de un proyecto para estudiantes Requisito 5:
     * Vista del estado para estudiantes
     */
    public String obtenerEstadoLegible(String estado) {
        if (estado == null) {
            return "Estado desconocido";
        }

        switch (estado) {
            case "EN_PRIMERA_EVALUACION_FORMATO_A":
                return "En primera evaluación formato A";
            case "EN_SEGUNDA_EVALUACION_FORMATO_A":
                return "En segunda evaluación formato A";
            case "EN_TERCERA_EVALUACION_FORMATO_A":
                return "En tercera evaluación formato A";
            case "FORMATO_A_APROBADO":
                return "Formato A aprobado";
            case "FORMATO_A_RECHAZADO":
                return "Formato A rechazado";
            case "EN_EVALUACION_FORMATO_B":
                return "En evaluación formato B";
            case "FORMATO_B_RECHAZADO":
                return "Formato B rechazado";
            case "FORMATO_B_APROBADO":
                return "Formato B aprobado";
            case "EN_DESARROLLO":
                return "En desarrollo";
            case "EN_EVALUACION_FINAL":
                return "En evaluación final";
            case "APROBADO":
                return "Aprobado";
            case "RECHAZADO_DEFINITIVO":
                return "Rechazado definitivamente";
            default:
                return "Estado: " + estado;
        }
    }

    // ========== MÉTODOS DE VALIDACIÓN PRIVADOS ==========
    /**
     * Valida un proyecto completo con todas sus reglas de negocio
     */
    private void validarProyecto(ProyectoGrado proyecto) throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarNoNulo(proyecto, "proyecto");

        StringBuilder errores = new StringBuilder();

        // Validar título usando ValidationUtil
        try {
            ValidationUtil.validarLongitud(proyecto.getTitulo(), "título", 10, 200);
        } catch (InvalidUserDataException e) {
            errores.append("- ").append(e.getMessage()).append("\n");
        }

        // Validar modalidad
        try {
            ValidationUtil.validarNoVacio(proyecto.getModalidad(), "modalidad");
            ProyectoGrado.Modalidad.fromValor(proyecto.getModalidad());
        } catch (InvalidUserDataException e) {
            errores.append("- ").append(e.getMessage()).append("\n");
        } catch (IllegalArgumentException e) {
            errores.append("- Modalidad no válida. Debe ser INVESTIGACION o PRACTICA_PROFESIONAL\n");
        }

        // Validar que si el director es OCASIONAL, debe tener codirector
        User director = userService.findByEmail(proyecto.getDirectorEmail());
        if ("OCASIONAL".equals(director.getTipoDocente())) {
            if (proyecto.getCodirectorEmail() == null || proyecto.getCodirectorEmail().trim().isEmpty()) {
                errores.append("- El director es ocasional y debe tener un codirector asignado.\n");
            } else {
                // Validar que el codirector exista
                try {
                    userService.findByEmail(proyecto.getCodirectorEmail());
                } catch (UserNotFoundException e) {
                    errores.append("- Codirector no encontrado con email: ").append(proyecto.getCodirectorEmail()).append("\n");
                }
            }
        }

        validarEmailUsuario(proyecto.getEstudiante1Email(), "estudiante1", errores);
        validarEmailUsuario(proyecto.getEstudiante2Email(), "estudiante2", errores);
        // Validar objetivos usando ValidationUtil
        try {
            ValidationUtil.validarLongitud(proyecto.getObjetivoGeneral(), "objetivo general", 20, 1000);
        } catch (InvalidUserDataException e) {
            errores.append("- ").append(e.getMessage()).append("\n");
        }

        try {
            ValidationUtil.validarLongitud(proyecto.getObjetivosEspecificos(), "objetivos específicos", 30, 2000);
        } catch (InvalidUserDataException e) {
            errores.append("- ").append(e.getMessage()).append("\n");
        }

        // Validar número de intento
        if (proyecto.getNumeroIntento() < 1 || proyecto.getNumeroIntento() > 3) {
            errores.append("- El número de intento debe estar entre 1 y 3\n");
        }

        if (errores.length() > 0) {
            throw new InvalidUserDataException(errores.toString());
        }
    }

    /**
     * Valida email de usuario y su existencia
     */
    private void validarEmailUsuario(String email, String rol, StringBuilder errores) {
        // Si el email es null, no hay nada que validar
        if (email == null) {
            return;
        }

        try {
            ValidationUtil.validarEmail(email, "email del " + rol);
            userService.findByEmail(email);
        } catch (InvalidUserDataException e) {
            errores.append("- Email del ").append(rol).append(" no válido: ").append(e.getMessage()).append("\n");
        } catch (UserNotFoundException e) {
            errores.append("- ").append(rol.substring(0, 1).toUpperCase()).append(rol.substring(1))
                    .append(" no encontrado con email: ").append(email).append("\n");
        }
    }

    /**
     * Valida que el estado sea válido
     */
    private void validarEstado(String estado) throws InvalidUserDataException {
        ValidationUtil.validarNoVacio(estado, "estado");

        try {
            EstadoEnum.fromValor(estado);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserDataException("Estado no válido: " + estado + ". Los estados válidos son: "
                    + getEstadosValidos());
        }
    }

    /**
     * Obtiene lista de estados válidos como string
     */
    private String getEstadosValidos() {
        StringBuilder estados = new StringBuilder();
        for (EstadoEnum estado : EstadoEnum.values()) {
            estados.append(estado.getValor()).append(", ");
        }
        return estados.substring(0, estados.length() - 2);
    }

    // ========== MÉTODOS DE SIMULACIÓN DE EMAILS ==========
    /**
     * Simula envío de email cuando se crea un proyecto
     */
    private void simularEnvioEmailCreacion(ProyectoGrado proyecto) {
        LOGGER.info("=== SIMULACIÓN ENVÍO EMAIL ===");
        LOGGER.info("ASUNTO: Nuevo proyecto de grado creado");
        LOGGER.info("DESTINATARIOS: " + proyecto.getDirectorEmail() + ", " + proyecto.getEstudiante1Email());
        if (proyecto.getCodirectorEmail() != null) {
            LOGGER.info("CC: " + proyecto.getCodirectorEmail());
        }
        LOGGER.info("MENSAJE: Se ha creado exitosamente el proyecto '" + proyecto.getTitulo()
                + "' y está en evaluación de formato A (intento " + proyecto.getNumeroIntento() + ")");
        LOGGER.info("=== FIN SIMULACIÓN EMAIL ===");
    }

    /**
     * Simula envío de email cuando se evalúa un formato A Requisito 3:
     * Notificación tras evaluación
     */
    private void simularEnvioEmailEvaluacion(ProyectoGrado proyecto, boolean aprobado,
            String observaciones, String coordinadorEmail) {
        LOGGER.info("=== SIMULACIÓN ENVÍO EMAIL ===");
        String resultado = aprobado ? "APROBADO" : "RECHAZADO";
        LOGGER.info("ASUNTO: Evaluación de Formato A - " + resultado);
        LOGGER.info("DESTINATARIOS: " + proyecto.getDirectorEmail() + ", " + proyecto.getEstudiante1Email());
        if (proyecto.getCodirectorEmail() != null) {
            LOGGER.info("CC: " + proyecto.getCodirectorEmail());
        }
        LOGGER.info("EVALUADOR: " + coordinadorEmail);
        LOGGER.info("PROYECTO: " + proyecto.getTitulo());
        LOGGER.info("RESULTADO: " + resultado);
        if (observaciones != null && !observaciones.trim().isEmpty()) {
            LOGGER.info("OBSERVACIONES: " + observaciones);
        }
        LOGGER.info("=== FIN SIMULACIÓN EMAIL ===");
    }

    /**
     * Simula envío de email cuando se procesa un reintento
     */
    private void simularEnvioEmailReintento(ProyectoGrado proyecto) {
        LOGGER.info("=== SIMULACIÓN ENVÍO EMAIL ===");
        LOGGER.info("ASUNTO: Nuevo intento de Formato A autorizado");
        LOGGER.info("DESTINATARIOS: " + proyecto.getDirectorEmail() + ", " + proyecto.getEstudiante1Email());
        if (proyecto.getCodirectorEmail() != null) {
            LOGGER.info("CC: " + proyecto.getCodirectorEmail());
        }
        LOGGER.info("PROYECTO: " + proyecto.getTitulo());
        LOGGER.info("MENSAJE: Se ha autorizado el intento " + (proyecto.getNumeroIntento() + 1)
                + " para el Formato A. Pueden subir una nueva versión.");
        LOGGER.info("=== FIN SIMULACIÓN EMAIL ===");
    }

    /**
     * Simula envío de email de rechazo definitivo
     */
    private void simularEnvioEmailRechazoDefinitivo(ProyectoGrado proyecto) {
        LOGGER.info("=== SIMULACIÓN ENVÍO EMAIL ===");
        LOGGER.info("ASUNTO: PROYECTO RECHAZADO DEFINITIVAMENTE");
        LOGGER.info("DESTINATARIOS: " + proyecto.getDirectorEmail() + ", " + proyecto.getEstudiante1Email());
        if (proyecto.getCodirectorEmail() != null) {
            LOGGER.info("CC: " + proyecto.getCodirectorEmail());
        }
        LOGGER.info("PROYECTO: " + proyecto.getTitulo());
        LOGGER.info("MENSAJE: El proyecto ha sido rechazado definitivamente después de 3 intentos. "
                + "El estudiante debe comenzar un nuevo proyecto desde cero.");
        LOGGER.info("=== FIN SIMULACIÓN EMAIL ===");
    }

    // ========== MÉTODOS ADICIONALES PARA COORDINADORES ==========
    /**
     * Obtiene proyectos por estado específico
     */
    public List<ProyectoGrado> obtenerProyectosPorEstado(String estado)
            throws InvalidUserDataException, UserNotFoundException {
        validarEstado(estado);

        List<ProyectoGrado> todosProyectos = obtenerTodosProyectos();
        return todosProyectos.stream()
                .filter(p -> estado.equals(p.getEstadoActual()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene estadísticas generales del sistema
     */
    public String obtenerEstadisticasGenerales() {
        try {
            List<ProyectoGrado> todosProyectos = proyectoRepository.obtenerTodosProyectos();

            StringBuilder stats = new StringBuilder();
            stats.append("=== ESTADÍSTICAS GENERALES ===\n");
            stats.append("Total de proyectos: ").append(todosProyectos.size()).append("\n");

            // Contar por modalidades
            long investigacion = todosProyectos.stream()
                    .filter(p -> "INVESTIGACION".equals(p.getModalidad()))
                    .count();
            long practicaProfesional = todosProyectos.stream()
                    .filter(p -> "PRACTICA_PROFESIONAL".equals(p.getModalidad()))
                    .count();

            stats.append("Proyectos de Investigación: ").append(investigacion).append("\n");
            stats.append("Proyectos de Práctica Profesional: ").append(practicaProfesional).append("\n");

            // Contar por estados
            stats.append("\n=== DISTRIBUCIÓN POR ESTADOS ===\n");
            for (EstadoEnum estado : EstadoEnum.values()) {
                long count = todosProyectos.stream()
                        .filter(p -> estado.getValor().equals(p.getEstadoActual()))
                        .count();
                if (count > 0) {
                    stats.append(obtenerEstadoLegible(estado.getValor())).append(": ").append(count).append("\n");
                }
            }

            return stats.toString();

        } catch (Exception e) {
            return "Error obteniendo estadísticas: " + e.getMessage();
        }
    }
}
