/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package co.unicauca.solid.access;

import co.unicauca.solid.domain.Programa;
import java.util.List;

/**
 *
 * @author crist
 */
public interface IProgramRepository {
    boolean insertarPrograma(Programa programa);
    Programa obtenerProgramaPorId(String idPrograma);
    List<Programa> obtenerTodosProgramas();
    boolean actualizarPrograma(Programa programa);
    boolean eliminarPrograma(String idPrograma);
}
