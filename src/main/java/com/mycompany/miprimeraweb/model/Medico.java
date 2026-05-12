package com.mycompany.miprimeraweb.model;

public class Medico {

    private int idMedico;
    private String nombres;
    private String apellidos;
    private int idEspecialidad;
    private String nombreEspecialidad;
    private String telefono;

    public Medico() {
    }

    public Medico(int idMedico, String nombres, String apellidos, int idEspecialidad, String nombreEspecialidad, String telefono) {
        this.idMedico = idMedico;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.idEspecialidad = idEspecialidad;
        this.nombreEspecialidad = nombreEspecialidad;
        this.telefono = telefono;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNombreCompleto() {
        String nom = nombres == null ? "" : nombres;
        String ape = apellidos == null ? "" : apellidos;
        return (nom + " " + ape).trim();
    }
}
