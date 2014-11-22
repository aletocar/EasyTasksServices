/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.easytasks.services.rest;

import com.easytasks.dataTransferObjects.*;
import com.easytasks.negocio.ABMUsuariosSBLocal;
import com.easytasks.negocio.excepciones.ExisteEntidadException;
import com.easytasks.negocio.excepciones.NoExisteEntidadException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
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

    @GET
    @Path("/saludar")
    public String saludar() {
        return "hola";
    }

    @GET
    @Path("/ChequeoDeVida")
    public String chequeo() {
        return usuarios.chequeoDeVida();
    }

    @PUT
    @Path("/agregarUsuario")
    @Consumes("application/json")
    public String agregarUsuario(DtoUsuario u) {
        try {
            usuarios.agregarUsuario(u);
            return usuarios.login(u.getNombreUsuario(), u.getContraseña());
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
            }
            catch(ExisteEntidadException e) {
                return e.getMessage();
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
            } catch (NoExisteEntidadException ex) {
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
            return null;
        }
    }

    @PUT
    @Path("/{user}/login")
    @Consumes("application/json")
    public String login(@QueryParam("password") String password, @PathParam("user") String user) {
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

}
