/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.solid.service;

import co.unicauca.solid.access.IProgramRepository;
import co.unicauca.solid.domain.Programa;
import java.util.List;

/**
 *
 * @author crist
 */
public class ProgramService {
    private final IProgramRepository programaRepository;
    
    public ProgramService(IProgramRepository programaRepository) {
        this.programaRepository = programaRepository;
    }
    
    public boolean crearPrograma(Programa programa) {
        return programaRepository.insertarPrograma(programa);
    }
    
    public Programa obtenerPrograma(String idPrograma) {
        return programaRepository.obtenerProgramaPorId(idPrograma);
    }
    
    public List<Programa> obtenerTodosProgramas() {
        return programaRepository.obtenerTodosProgramas();
    }
    
    public boolean actualizarPrograma(Programa programa) {
        return programaRepository.actualizarPrograma(programa);
    }
    
    public boolean eliminarPrograma(String idPrograma) {
        return programaRepository.eliminarPrograma(idPrograma);
    }
    
    // Obtener programas como opciones para combobox
    public String[] obtenerOpcionesProgramas() {
        List<Programa> programas = obtenerTodosProgramas();
        String[] opciones = new String[programas.size()];
        
        for (int i = 0; i < programas.size(); i++) {
            opciones[i] = programas.get(i).getIdPrograma();
        }
        
        return opciones;
    }
    
    // Obtener nombre completo del programa
    public String obtenerNombreCompletoPrograma(String idPrograma) {
        Programa programa = obtenerPrograma(idPrograma);
        return programa != null ? programa.getNombreCompleto() : "Programa no encontrado";
    }
}
