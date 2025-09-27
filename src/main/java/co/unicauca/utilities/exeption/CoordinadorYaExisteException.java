/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.utilities.exeption;

/**
 *
 * @author crist
 */
public class CoordinadorYaExisteException extends Exception {
    public CoordinadorYaExisteException(String programa) {
        super("Ya existe un coordinador para el programa: " + programa);
    }
}