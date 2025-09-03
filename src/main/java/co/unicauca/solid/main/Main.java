/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */
package co.unicauca.solid.main;

import co.unicauca.presentation.GUIDashboard;
import co.unicauca.solid.access.ProgramRepository;
import co.unicauca.solid.domain.Programa;
import co.unicauca.solid.service.ProgramService;
import java.util.List;

/**
 *
 * @author crist
 */
public class Main {

    public static void main(String[] args) {
        ProgramRepository programaRepository = new ProgramRepository();
        ProgramService programaService = new ProgramService(programaRepository);
        List<Programa> programas = programaService.obtenerTodosProgramas();
        System.out.println("Programas disponibles:");
        for (Programa programa : programas) {
            System.out.println("- " + programa.getIdPrograma() + ": " + programa.getNombreCompleto());
        }
        GUIDashboard dashboard = new GUIDashboard();
        dashboard.setVisible(true);
        System.out.println("Hello World!");
    }
}
