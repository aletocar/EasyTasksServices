/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easytasks.services.rest;

import com.easytasks.dataTransferObjects.*;
import com.easytasks.negocio.logica.ABMRealizablesSBLocal;
import com.easytasks.negocio.logica.ABMUsuariosSBLocal;
import com.easytasks.negocio.logica.ManejadorTareasSBLocal;
import com.easytasks.negocio.excepciones.EntidadEliminadaIncorrectamenteException;
import com.easytasks.negocio.excepciones.EntidadModificadaIncorrectamenteException;
import com.easytasks.negocio.excepciones.EntidadNoCreadaCorrectamenteException;
import com.easytasks.negocio.excepciones.ExisteEntidadException;
import com.easytasks.negocio.excepciones.NoExisteEntidadException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * REST Web Service
 *
 * @author alejandrotocar
 */
@Path("generic/")
@Stateless
public class ServiciosWS {

    @Context
    private UriInfo context;

    @EJB
    ABMUsuariosSBLocal usuarios;

    @EJB
    ABMRealizablesSBLocal realizables;

    @EJB
    ManejadorTareasSBLocal tareas;

    /**
     * Creates a new instance of ServiciosWS
     */
    public ServiciosWS() {

    }

    /**
     * Retrieves representation of an instance of
     * com.easytasks.services.rest.ServiciosWS
     *
     * @return an instance of java.lang.String
     */
    @GET
    @Produces("application/xml")
    public String getXml() {
        //TODO return proper representation object
        throw new UnsupportedOperationException();
    }

    /**
     * PUT method for updating or creating an instance of ServiciosWS
     *
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }

    // <editor-fold defaultstate="collapsed" desc=" Usuario ">
    @PUT
    @Path("/agregarUsuario")
    @Consumes("application/json")
    public String agregarUsuario(DtoUsuario u) {
        try {
            usuarios.agregarUsuario(u);
            return usuarios.login(u.getNombreUsuario(), u.getContraseña());
        } catch (EntidadNoCreadaCorrectamenteException e) {
            return e.getMessage();
        } catch (ExisteEntidadException e) {
            return "Ya existe un usuario con el Nombre de Usuario: " + u.getNombreUsuario() + ". Ingrese un nombre de usuario único";
        } catch (NoExisteEntidadException n) {
            return "Hubo un problema al intentar ingresar su usuario al sistema. Sin embargo, su usuario quedó registrado. Por favor intente ingresar normalmente";
        } catch (Exception ee) {
            return "Ocurrió un error inesperado al ingresar el usuario " + u.getNombreUsuario();
        }

    }

    @POST
    @Path("/agregarContacto")
    @Consumes("application/json")
    public String agregarContacto(@QueryParam("usuario") String usuario, @QueryParam("contacto") String contacto, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, usuario)) {
            try {
                usuarios.agregarContacto(usuario, contacto);
                return "OK";
            } catch (NoExisteEntidadException ex) {
                return "No existe uno de los usuarios que desea agregar";
            } catch (ExisteEntidadException | EntidadModificadaIncorrectamenteException e) {
                return e.getMessage();
            } catch (Exception ee) {
                return "Ocurrió un error inesperado al ingresar el contacto";
            }
        } else {
            return "debe estar logueado para realizar esta acción";
        }
    }

    @POST
    @Path("/modificarUsuario")
    @Consumes("application/json")
    public String modificarUsuario(DtoUsuario usuario, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, usuario.getNombreUsuario())) {
            try {
                usuarios.modificarUsuario(usuario);
                return "OK";
            } catch (NoExisteEntidadException | EntidadModificadaIncorrectamenteException ex) {
                return ex.getMessage();
            } catch (Exception e) {
                return "Ocurrió un error inesperado al modificar el usuario " + usuario.getNombreUsuario();
            }
        } else {
            return "Debe estar logueado para realizar esta acción";
        }
    }

    @DELETE
    @Path("/borrarUsuario")
    @Consumes("application/json")
    public String borrarUsuario(@QueryParam("nombreUsuario") String nombreUsuario, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                usuarios.borrarUsuario(nombreUsuario);
                return "OK";
            } catch (NoExisteEntidadException ex) {
                return "No existe el usuario que desea borrar.";
            }
        } else {
            return "Debe loguearse para realizar esta acción";
        }
    }

    @GET
    @Path("/obtenerUsuario")
    @Consumes("application/json")
    public DtoUsuario obtenerUsuario(@QueryParam("nombreUsuario") String nombreUsuario) {

        try {
            return usuarios.buscarUsuario(nombreUsuario);
        } catch (NoExisteEntidadException ex) {
            DtoUsuario ret = new DtoUsuario();
            ret.setMessage(ex.getMessage());
            ret.setTransferOk(false);
            return ret;
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Login ">
    @PUT
    @Path("/login")
    @Consumes("application/json")
    public String login(@QueryParam("password") String password, @QueryParam("user") String user) {
        try {
            String t = usuarios.login(user, password);
            if (t.equals("")) {
                t = "La combinación de usuario y contraseña no es correcta. Por favor intente nuevamente";
            }
            return t;
        } catch (ExisteEntidadException | NoExisteEntidadException e) {
            return e.getMessage();
        }
    }

    @DELETE
    @Path("/{token}/logout")
    @Consumes("application/json")
    public String logout(@PathParam("token") String t) {
        try {
            usuarios.logout(t);
            return "OK";
        } catch (NoExisteEntidadException ex) {
            return ex.getMessage();
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Proyecto ">
    @PUT
    @Path("/agregarProyecto")
    @Consumes("application/json")
    public String agregarProyecto(DtoProyecto p, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, p.getResponsable().getNombreUsuario())) {
            try {
                realizables.agregarProyecto(p);
                return "OK";
            } catch (ExisteEntidadException e) {
                return "Ya existe un proyecto con el Nombre: " + p.getNombre() + ". Ingrese un nombre de proyecto único";
            } catch (EntidadNoCreadaCorrectamenteException en) {
                return en.getMessage();
            } catch (Exception ee) {
                return "Ocurrió un error inesperado al agregar el proyecto " + p.getNombre();
            }
        } else {
            return "Debe estar logueado para realizar esta acción";
        }
    }

    /**
     *
     * @param proyecto
     * @param token
     * @return
     */
    @POST
    @Path("/modificarProyecto")
    @Consumes("application/json")
    public String modificarProyecto(DtoProyecto proyecto, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, proyecto.getResponsable().getNombreUsuario())) {
            try {
                realizables.modificarProyecto(proyecto);
                return "OK";
            } catch (NoExisteEntidadException | EntidadModificadaIncorrectamenteException ex) {
                return ex.getMessage();
            } catch (Exception e) {
                return "Ocurrió un error inesperado al modificar el proyecto " + proyecto.getNombre();
            }
        } else {
            return "Debe estar logueado para realizar esta acción";
        }
    }

    @DELETE
    @Path("/borrarProyecto")
    @Consumes("application/json")
    public String borrarProyecto(@QueryParam("nombreProyecto") String nombreProyecto, @QueryParam("nombreResponsable") String nombreResponsable, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, nombreResponsable)) {
            try {

                realizables.borrarProyecto(nombreProyecto, nombreResponsable);
                return "OK";
            } catch (NoExisteEntidadException ex) {
                return "No existe el usuario que desea borrar.";
            }
        } else {
            return "Debe loguearse para realizar esta acción";
        }
    }

    @POST
    @Path("/asignarUsuarioAProyecto")
    public String asignarUsuarioAProyecto(@QueryParam("nombreProyecto") String nombreProyecto, @QueryParam("nombreResponsable") String nombreResponsable, @QueryParam("nombreUsuario") String nombreUsuario, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, nombreResponsable)) {
            try {
                realizables.asignarUsuarioAProyecto(nombreProyecto, nombreResponsable, nombreUsuario);
                return "OK";
            } catch (NoExisteEntidadException ex) {
                return ex.getMessage();
            }
        } else {
            return "Debe loguearse para realizar esta acción";
        }
    }

    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Tarea ">
    @PUT
    @Path("/agregarTarea")
    @Consumes("application/json")
    public String agregarTarea(DtoTarea t, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, t.getProyecto().getResponsable().getNombreUsuario())) {
            try {
                realizables.agregarTarea(t);
                return "OK";
            } catch (ExisteEntidadException | EntidadNoCreadaCorrectamenteException e) {
                return e.getMessage();
            } catch (Exception ee) {
                return "Ocurrió un error inesperado al agregar la tarea " + t.getNombre();
            }
        } else {
            return "Debe estar logueado para realizar esta acción";
        }
    }

    @POST
    @Path("/modificarTarea")
    @Consumes("application/json")
    public String modificarTarea(DtoTarea tarea, @QueryParam("token") String token, @QueryParam("nombreUsuario") String nombreUsuario) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                realizables.modificarTarea(tarea, nombreUsuario);
                return "OK";
            } catch (NoExisteEntidadException | EntidadModificadaIncorrectamenteException ex) {
                return ex.getMessage();
            } catch (Exception e) {
                return "Ocurrió un error inesperado al modificar la tarea " + tarea.getNombre();
            }
        } else {
            return "Debe estar logueado para realizar esta acción";
        }
    }

    @DELETE
    @Path("/borrarTarea")
    public String borrarTarea(@QueryParam("nombreTarea") String nombreTarea, @QueryParam("nombreProyecto") String nombreProyecto, @QueryParam("nombreResponsable") String nombreResponsable, @QueryParam("token") String token, @QueryParam("nombreUsuario") String nombreUsuario) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                realizables.borrarTarea(nombreTarea, nombreProyecto, nombreResponsable, nombreUsuario);
                return "OK";
            } catch (NoExisteEntidadException ex) {
                return "No existe el usuario que desea borrar.";
            } catch (EntidadEliminadaIncorrectamenteException ex) {
                return ex.getMessage();
            }
        } else {
            return "Debe loguearse para realizar esta acción";
        }
    }

    @POST
    @Path("/completarTarea")
    public String completarTarea(@QueryParam("nombreTarea") String nombreTarea,
            @QueryParam("nombreProyecto") String nombreProyecto,
            @QueryParam("nombreResponsable") String nombreResponsable,
            @QueryParam("token") String token,
            @QueryParam("nombreUsuario") String nombreUsuario) {

        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                tareas.completarTarea(nombreTarea, nombreProyecto, nombreResponsable, nombreUsuario);
                return "OK";
            } catch (EntidadModificadaIncorrectamenteException | NoExisteEntidadException e) {
                return e.getMessage();
            } catch (Exception e) {
                return "Error Inesperado";
            }
        } else {
            return "Debe loguearse para realizar esta acción";
        }
    }

    @POST
    @Path("/agregarResponsableATarea")
    public String agregarResponsableATarea(
            @QueryParam("nombreTarea") String nombreTarea,
            @QueryParam("nombreProyecto") String nombreProyecto,
            @QueryParam("nombreResponsable") String nombreResponsable,
            @QueryParam("token") String token,
            @QueryParam("nombreUsuario") String nombreUsuario,
            @QueryParam("nombreUsuarioAAgregar") String nombreUsuarioAAgregar
    ) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                tareas.agregarResponsable(nombreTarea, nombreProyecto, nombreResponsable, nombreUsuario, nombreUsuarioAAgregar);
                return "OK";
            } catch (EntidadModificadaIncorrectamenteException | NoExisteEntidadException e) {
                return e.getMessage();
            } catch (Exception e) {
                return "Error Inesperado";
            }
        } else {
            return "Debe loguearse para realizar esta acción";
        }
    }

    @POST
    @Path("/agregarSubTarea")
    public String agregarSubtarea(
            @QueryParam("nombreTareaPadre") String nombreTareaPadre,
            @QueryParam("nombreProyecto") String nombreProyecto,
            @QueryParam("nombreResponsable") String nombreResponsable,
            @QueryParam("token") String token,
            @QueryParam("nombreTareaHija") String nombreTareaHija
    ) {
        if (usuarios.estaLogueado(token, nombreResponsable)) {
            try {
                tareas.agregarSubTarea(nombreTareaPadre, nombreTareaHija, nombreProyecto, nombreResponsable);
                return "OK";
            } catch (EntidadModificadaIncorrectamenteException | NoExisteEntidadException e) {
                return e.getMessage();
            } catch (Exception e) {
                return "Error Inesperado";
            }
        } else {
            return "Debe loguearse para realizar esta acción";
        }
    }

    @POST
    @Path("/delegarTarea")
    public String delegarTarea(
            @QueryParam("nombreTarea") String nombreTarea,
            @QueryParam("nombreProyecto") String nombreProyecto,
            @QueryParam("nombreResponsable") String nombreResponsable,
            @QueryParam("nombreUsuarioActual") String nombreUsuarioActual,
            @QueryParam("nombreUsuarioDelegado") String nombreUsuarioDelegado,
            @QueryParam("token") String token
    ) {
        if (usuarios.estaLogueado(token, nombreUsuarioActual)) {
            try {
                tareas.delegarTarea(nombreTarea, nombreProyecto, nombreResponsable, nombreUsuarioActual, nombreUsuarioDelegado);
                return "OK";
            } catch (EntidadModificadaIncorrectamenteException | NoExisteEntidadException e) {
                return e.getMessage();
            } catch (Exception e) {
                return "Error Inesperado";
            }
        } else {
            return "Debe loguearse para realizar esta acción";
        }
    }

    @GET
    @Path("/consultaTareasRealizadas")
    @Produces("application/json")
    public List<DtoTarea> consultaTareasRealizadas(
            @QueryParam("nombreUsuario") String nombreUsuario,
            @QueryParam("token") String token
    ) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                return tareas.consultarTareasRealizadas(nombreUsuario);
            } catch (NoExisteEntidadException e) {
                List<DtoTarea> ret = new ArrayList<>();
                DtoTarea t = new DtoTarea();
                t.setMessage(e.getMessage());
                t.setTransferOk(false);
                ret.add(t);
                return ret;
            }
        } else {
            List<DtoTarea> ret = new ArrayList<>();
            DtoTarea t = new DtoTarea();
            t.setMessage("El usuario consultante debe estar logueado para realizar la consulta");
            t.setTransferOk(false);
            ret.add(t);
            return ret;
        }
    }

    @GET
    @Path("/consultaTareasRealizadasResponsable")
    @Produces("application/json")
    public List<DtoTarea> consultaTareasRealizadasResponsable(
            @QueryParam("nombreUsuario") String nombreUsuario,
            @QueryParam("token") String token
    ) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                return tareas.consultarTareasRealizadasResponsable(nombreUsuario);
            } catch (NoExisteEntidadException e) {
                List<DtoTarea> ret = new ArrayList<>();
                DtoTarea t = new DtoTarea();
                t.setMessage(e.getMessage());
                t.setTransferOk(false);
                ret.add(t);
                return ret;
            }
        } else {
            List<DtoTarea> ret = new ArrayList<>();
            DtoTarea t = new DtoTarea();
            t.setMessage("El usuario consultante debe estar logueado para realizar la consulta");
            t.setTransferOk(false);
            ret.add(t);
            return ret;
        }
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc=" Social ">
    @GET
    @Path("/conectarSocial")
    public String conectarSocial(@QueryParam("redSocial") String redSocial, @QueryParam("nombreUsuario") String nombreUsuario, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                return usuarios.conectar(nombreUsuario, redSocial);
            } catch (Exception e) {
                return e.getMessage();
            }
        } else {
            return "debe estar logueado para realizar esta accion";
        }
    }

    @POST
    @Path("/ingresarPin")
    public String ingresarPin(@QueryParam("pin") String pin, @QueryParam("nombreUsuario") String nombreUsuario, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                usuarios.ingresarPin(nombreUsuario, pin);
                return "OK";
            } catch (Exception e) {
                return "Error";
            }
        } else {
            return "debe estar logueado para realizar esta accion";
        }
    }

    @POST
    @Path("/postear")
    public String postear(@QueryParam("post") String post, @QueryParam("nombreUsuario") String nombreUsuario, @QueryParam("token") String token, @QueryParam("redSocial") String redSocial) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                usuarios.postear(nombreUsuario, post, redSocial);
                return "OK";
            } catch (EJBException e) {
                return e.getMessage();
            } catch (Exception e) {
                return "error al postear";
            }
        } else {
            return "debe estar logueado para realizar esta accion";
        }
    }

    @POST
    @Path("/desconectarSocial")
    public String desconectarSocial(@QueryParam("redSocial") String redSocial, @QueryParam("nombreUsuario") String nombreUsuario, @QueryParam("token") String token) {
        if (usuarios.estaLogueado(token, nombreUsuario)) {
            try {
                usuarios.desconectar(nombreUsuario, redSocial);
                return "OK";
            } catch (EJBException e) {
                return e.getMessage();
            } catch (Exception e) {
                return "error al desconectar";
            }
        } else {
            return "debe estar logueado para realizar esta accion";
        }
    }
    // </editor-fold>
}
