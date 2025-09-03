package co.unicauca.solid.service;

import co.unicauca.solid.access.IProyectoGradoRepository;
import co.unicauca.solid.domain.ProyectoGrado;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import co.unicauca.utilities.validators.ValidationUtil;
import java.util.List;

public class ProyectoGradoService {

    private final IProyectoGradoRepository proyectoRepository;
    private final UserService userService;

    public ProyectoGradoService(IProyectoGradoRepository proyectoRepository, UserService userService) {
        this.proyectoRepository = proyectoRepository;
        this.userService = userService;
    }

    public int crearProyecto(ProyectoGrado proyecto) throws InvalidUserDataException, UserNotFoundException {
        validarProyecto(proyecto);
        return proyectoRepository.insertarProyecto(proyecto);
    }

    public ProyectoGrado obtenerProyecto(int idProyecto) throws UserNotFoundException {
        ProyectoGrado proyecto = proyectoRepository.obtenerProyectoPorId(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }
        return proyecto;
    }

    public List<ProyectoGrado> obtenerProyectosPorEstudiante(String estudianteEmail)
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarEmail(estudianteEmail, "email del estudiante");

        List<ProyectoGrado> proyectos = proyectoRepository.obtenerProyectosPorEstudiante(estudianteEmail);
        if (proyectos.isEmpty()) {
            throw new UserNotFoundException("No se encontraron proyectos para el estudiante: " + estudianteEmail);
        }
        return proyectos;
    }

    public List<ProyectoGrado> obtenerProyectosPorDirector(String directorEmail)
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarEmail(directorEmail, "email del director");

        List<ProyectoGrado> proyectos = proyectoRepository.obtenerProyectosPorDirector(directorEmail);
        if (proyectos.isEmpty()) {
            throw new UserNotFoundException("No se encontraron proyectos para el director: " + directorEmail);
        }
        return proyectos;
    }

    public List<ProyectoGrado> obtenerTodosProyectos() throws UserNotFoundException {
        List<ProyectoGrado> proyectos = proyectoRepository.obtenerTodosProyectos();
        if (proyectos.isEmpty()) {
            throw new UserNotFoundException("No se encontraron proyectos en el sistema");
        }
        return proyectos;
    }

    public boolean actualizarProyecto(ProyectoGrado proyecto) throws InvalidUserDataException, UserNotFoundException {
        validarProyecto(proyecto);

        // Verificar que el proyecto existe
        ProyectoGrado proyectoExistente = obtenerProyecto(proyecto.getIdProyecto());
        if (proyectoExistente == null) {
            throw new UserNotFoundException("Proyecto con ID " + proyecto.getIdProyecto() + " no encontrado");
        }

        return proyectoRepository.actualizarProyecto(proyecto);
    }

    public boolean actualizarEstadoProyecto(int idProyecto, String nuevoEstado)
            throws UserNotFoundException, InvalidUserDataException {

        // Verificar que el proyecto existe
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }

        validarEstado(nuevoEstado);
        return proyectoRepository.actualizarEstadoProyecto(idProyecto, nuevoEstado);
    }

    public boolean incrementarIntento(int idProyecto) throws UserNotFoundException {
        // Verificar que el proyecto existe
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }

        return proyectoRepository.incrementarIntento(idProyecto);
    }

    public boolean marcarRechazoDefinitivo(int idProyecto) throws UserNotFoundException {
        // Verificar que el proyecto existe
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }

        return proyectoRepository.marcarRechazoDefinitivo(idProyecto);
    }

    public boolean eliminarProyecto(int idProyecto) throws UserNotFoundException {
        // Verificar que el proyecto existe
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }

        return proyectoRepository.eliminarProyecto(idProyecto);
    }

    // Métodos de negocio
    public boolean puedeReintentarFormato(int idProyecto) throws UserNotFoundException {
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        return proyecto != null && proyecto.puedeReintentar();
    }

    public boolean avanzarEstadoProyecto(int idProyecto, String siguienteEstado)
            throws UserNotFoundException, InvalidUserDataException {
        return actualizarEstadoProyecto(idProyecto, siguienteEstado);
    }

    public boolean rechazarProyecto(int idProyecto) throws UserNotFoundException {
        ProyectoGrado proyecto = obtenerProyecto(idProyecto);
        if (proyecto == null) {
            throw new UserNotFoundException("Proyecto con ID " + idProyecto + " no encontrado");
        }

        if (proyecto.puedeReintentar()) {
            incrementarIntento(idProyecto);
            try {
                return actualizarEstadoProyecto(idProyecto, ProyectoGrado.Estado.FORMATO_A_RECHAZADO.getValor());
            } catch (InvalidUserDataException e) {
                return false;
            }
        } else {
            marcarRechazoDefinitivo(idProyecto);
            try {
                return actualizarEstadoProyecto(idProyecto, ProyectoGrado.Estado.RECHAZADO_DEFINITIVO.getValor());
            } catch (InvalidUserDataException e) {
                return false;
            }
        }
    }

    // Métodos de validación mejorados
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

        // Validar emails usando ValidationUtil
        try {
            userService.findByEmail(proyecto.getDirectorEmail());
        } catch (InvalidUserDataException e) {
            errores.append("- Email del director no válido: ").append(e.getMessage()).append("\n");
        } catch (UserNotFoundException e) {
            errores.append("- Director no encontrado con email: ").append(proyecto.getDirectorEmail()).append("\n");
        }

        try {
            userService.findByEmail(proyecto.getCodirectorEmail());
        } catch (InvalidUserDataException e) {
            errores.append("- Email del codirector no válido: ").append(e.getMessage()).append("\n");
        } catch (UserNotFoundException e) {
            errores.append("- Codirector no encontrado con email: ").append(proyecto.getCodirectorEmail()).append("\n");
        }

        try {
            userService.findByEmail(proyecto.getEstudianteEmail());
        } catch (InvalidUserDataException e) {
            errores.append("- Email del estudiante no válido: ").append(e.getMessage()).append("\n");
        } catch (UserNotFoundException e) {
            errores.append("- Estudiante no encontrado con email: ").append(proyecto.getEstudianteEmail()).append("\n");
        }

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

        if (errores.length() > 0) {
            throw new InvalidUserDataException(errores.toString());
        }
    }

    private void validarEstado(String estado) throws InvalidUserDataException {
        ValidationUtil.validarNoVacio(estado, "estado");

        try {
            ProyectoGrado.Estado.fromValor(estado);
        } catch (IllegalArgumentException e) {
            throw new InvalidUserDataException("Estado no válido: " + estado + ". Los estados válidos son: "
                    + getEstadosValidos());
        }
    }

    private String getEstadosValidos() {
        StringBuilder estados = new StringBuilder();
        for (ProyectoGrado.Estado estado : ProyectoGrado.Estado.values()) {
            estados.append(estado.getValor()).append(", ");
        }
        return estados.substring(0, estados.length() - 2);
    }
}
