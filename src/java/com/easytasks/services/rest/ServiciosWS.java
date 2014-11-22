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
            return "OK";
        } catch (ExisteEntidadException e) {
            return "Ya existe un usuario con el Nombre de Usuario: " + u.getNombreUsuario() + ". Ingrese un nombre de usuario único";
        } catch (Exception ee) {
            return "Ocurrió un error inesperado al ingresar el usuario " + u.getNombreUsuario();
        }

    }

    @POST
    @Path("/agregarContacto")
    @Consumes("application/json")
    public String agregarContacto(@QueryParam("usuario") String usuario, @QueryParam("contacto") String contacto) {
        try {
            usuarios.agregarContacto(usuario, contacto);
            return "OK";
        } catch (NoExisteEntidadException ex) {
            return "No existe uno de los usuarios que desea agregar";
        }
    }

    @POST
    @Path("/modificarUsuario")
    @Consumes("application/json")
    public String modificarUsuario(DtoUsuario usuario) {
        try {
            usuarios.modificarUsuario(usuario);
            return "OK";
        } catch (NoExisteEntidadException ex) {
            return ex.getMessage();
        } catch (Exception e) {
            return "Ocurrió un error inesperado al modificar el usuario " + usuario.getNombreUsuario();

        }
    }

    @DELETE
    @Path("/borrarUsuario")
    @Consumes("application/json")
    public String borrarUsuario(@QueryParam("nombreUsuario") String nombreUsuario) {
        try {
            usuarios.borrarUsuario(nombreUsuario);
            return "OK";
        } catch (NoExisteEntidadException ex) {
            return "No existe el usuario que desea borrar.";
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
}
