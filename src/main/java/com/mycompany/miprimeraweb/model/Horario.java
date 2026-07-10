package com.mycompany.miprimeraweb.model;

public class Horario {

    private int idHorario;
    private int idMedico;
    private String dia;
    private String horaInicio;
    private String horaFin;
    private String medicoNombreCompleto;
    private String especialidad;
    private String usuarioRegistro;
    private String fechaRegistro;

    public Horario() {
    }

    public Horario(int idHorario, int idMedico, String dia, String horaInicio, String horaFin,
            String medicoNombreCompleto, String especialidad, String usuarioRegistro, String fechaRegistro) {
        this.idHorario = idHorario;
        this.idMedico = idMedico;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.medicoNombreCompleto = medicoNombreCompleto;
        this.especialidad = especialidad;
        this.usuarioRegistro = usuarioRegistro;
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdHorario() {
        return idHorario;
    }

    public void setIdHorario(int idHorario) {
        this.idHorario = idHorario;
    }

    public int getIdMedico() {
        return idMedico;
    }

    public void setIdMedico(int idMedico) {
        this.idMedico = idMedico;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public String getMedicoNombreCompleto() {
        return medicoNombreCompleto;
    }

    public void setMedicoNombreCompleto(String medicoNombreCompleto) {
        this.medicoNombreCompleto = medicoNombreCompleto;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }

    public String getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(String usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
