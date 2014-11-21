/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.easytasks.services.rest;

import com.easytasks.dataTransferObjects.*;
import com.easytasks.negocio.ABMUsuariosSB;
import com.easytasks.negocio.ABMUsuariosSBLocal;
import com.easytasks.negocio.excepciones.ExisteEntidadException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.PersistenceException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
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
     * Retrieves representation of an instance of com.easytasks.services.rest.ServiciosWS
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
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Consumes("application/xml")
    public void putXml(String content) {
    }
    
    @GET
    @Path("/saludar")
    public String saludar(){
        return "hola";
    }
    
    @GET
    @Path("/ChequeoDeVida")
    public String chequeo(){
        return usuarios.chequeoDeVida();
    }
    @PUT
    @Path("/agregarUsuario")
    @Consumes("application/json")
    public String agregarUsuario(DtoUsuario u){
        try{ 
            usuarios.agregarUsuario(u);
            return "OK";
        }catch(ExisteEntidadException e){
            return "Ya existe un usuario con el Nombre de Usuario: " + u.getNombreUsuario() + ". Ingrese un nombre de usuario único";
        }
        catch(Exception ee){
            return "Ocurrió un error inesperado al ingresar el usuario " + u.getNombreUsuario();
        }
        
    }
    
    @POST
    @Path("/agregarContacto")
    @Consumes("application/json")
    public void agregarContacto(@QueryParam("usuario")String usuario, @QueryParam("contacto")String contacto){
        usuarios.agregarContacto(usuario, contacto);
    }
    
    @POST
    @Path("/modificarUsuario")
    @Consumes("application/json")
    public void modificarUsuario(DtoUsuario usuario){       
        usuarios.modificarUsuario(usuario);
    }
    
    @DELETE
    @Path("/borrarUsuario")
    @Consumes("application/json")
    public void borrarUsuario(@QueryParam("nombreUsuario")String nombreUsuario){        
        usuarios.borrarUsuario(nombreUsuario);
        
    }
        
    @GET
    @Path("/obtenerUsuario")
    @Consumes("application/json")
    public DtoUsuario obtenerUsuario(@QueryParam("nombreUsuario")String nombreUsuario){
        return usuarios.buscarUsuario(nombreUsuario);
    }
}
