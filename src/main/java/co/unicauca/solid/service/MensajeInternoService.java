/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.service;

/**
 *
 * @author crist
 */

import co.unicauca.solid.access.IMensajeInternoRepository;
import co.unicauca.solid.domain.MensajeInterno;
import co.unicauca.solid.domain.Usuario;
import co.unicauca.utilities.exeption.InvalidUserDataException;
import co.unicauca.utilities.exeption.UserNotFoundException;
import co.unicauca.utilities.validators.ValidationUtil;

import java.util.List;

public class MensajeInternoService {
    private final IMensajeInternoRepository mensajeRepository;
    private final UserService userService;

    public MensajeInternoService(IMensajeInternoRepository mensajeRepository, UserService userService) {
        this.mensajeRepository = mensajeRepository;
        this.userService = userService;
    }

    /**
     * Permite a un estudiante enviar un mensaje a uno o varios docentes.
     */
    public int enviarMensaje(String emailEstudiante, String emailsDocentes, String asunto, String cuerpo, byte[] documentoAdjunto, String nombreArchivo)
            throws InvalidUserDataException, UserNotFoundException {

        ValidationUtil.validarEmail(emailEstudiante, "email del estudiante");
        ValidationUtil.validarNoVacio(emailsDocentes, "emails de los docentes");
        ValidationUtil.validarNoVacio(asunto, "asunto");
        ValidationUtil.validarNoVacio(cuerpo, "cuerpo del mensaje");

        // Validar que el remitente es un estudiante
        Usuario remitente = userService.findByEmail(emailEstudiante);
        if (!(remitente instanceof co.unicauca.solid.domain.Estudiante)) {
            throw new InvalidUserDataException("Solo los estudiantes pueden enviar mensajes de propuesta.");
        }

        // Validar que los destinatarios son docentes
        String[] listaEmails = emailsDocentes.split(",");
        for (String emailDoc : listaEmails) {
            String emailLimpio = emailDoc.trim();
            if (!emailLimpio.isEmpty()) {
                Usuario destinatario = userService.findByEmail(emailLimpio);
                if (!(destinatario instanceof co.unicauca.solid.domain.Docente)) {
                    throw new InvalidUserDataException("El destinatario " + emailLimpio + " no es un docente.");
                }
            }
        }

        MensajeInterno mensaje = new MensajeInterno(emailEstudiante, emailsDocentes, asunto, cuerpo);
        mensaje.setDocumentoAdjunto(documentoAdjunto);
        mensaje.setNombreArchivo(nombreArchivo);

        return mensajeRepository.enviarMensaje(mensaje);
    }

    /**
     * Obtiene todos los mensajes enviados por un estudiante.
     */
    public List<MensajeInterno> obtenerMensajesEnviadosPorEstudiante(String emailEstudiante)
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarEmail(emailEstudiante, "email del estudiante");
        Usuario estudiante = userService.findByEmail(emailEstudiante);
        if (!(estudiante instanceof co.unicauca.solid.domain.Estudiante)) {
            throw new InvalidUserDataException("El usuario no es un estudiante.");
        }
        return mensajeRepository.obtenerMensajesPorRemitente(emailEstudiante);
    }

    /**
     * Obtiene todos los mensajes recibidos por un docente.
     */
    public List<MensajeInterno> obtenerMensajesRecibidosPorDocente(String emailDocente)
            throws InvalidUserDataException, UserNotFoundException {
        ValidationUtil.validarEmail(emailDocente, "email del docente");
        Usuario docente = userService.findByEmail(emailDocente);
        if (!(docente instanceof co.unicauca.solid.domain.Docente)) {
            throw new InvalidUserDataException("El usuario no es un docente.");
        }
        return mensajeRepository.obtenerMensajesPorDestinatario(emailDocente);
    }

    /**
     * Marca un mensaje como leído.
     */
    public boolean marcarMensajeComoLeido(int idMensaje) {
        return mensajeRepository.marcarComoLeido(idMensaje);
    }
}