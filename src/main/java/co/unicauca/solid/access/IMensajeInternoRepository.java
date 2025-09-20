/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.unicauca.solid.access;

/**
 *
 * @author crist
 */

import co.unicauca.solid.domain.MensajeInterno;
import java.util.List;

/**
 * Interfaz para el repositorio de mensajes internos.
 */
public interface IMensajeInternoRepository {
    int enviarMensaje(MensajeInterno mensaje);
    List<MensajeInterno> obtenerMensajesPorRemitente(String remitenteEmail);
    List<MensajeInterno> obtenerMensajesPorDestinatario(String destinatarioEmail);
    MensajeInterno obtenerMensajePorId(int idMensaje);
    boolean marcarComoLeido(int idMensaje);
    boolean eliminarMensaje(int idMensaje);
}
