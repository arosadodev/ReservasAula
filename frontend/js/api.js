async function peticion(ruta, opciones = {}) {
  const configuracion = { ...opciones, headers: { ...(opciones.headers || {}) } };

  // Solo se envía Content-Type cuando existe un cuerpo JSON.
  // Así las peticiones GET no provocan un preflight CORS innecesario.
  if (configuracion.body && !configuracion.headers["Content-Type"]) {
    configuracion.headers["Content-Type"] = "application/json";
  }

  let respuesta;
  try {
    respuesta = await fetch(API_URL + ruta, configuracion);
  } catch (error) {
    const excepcion = new Error(
      "No se puede conectar con el servidor. Comprueba que Spring Boot está iniciado en el puerto 8080."
    );
    excepcion.causa = error;
    throw excepcion;
  }

  if (![200, 201, 204].includes(respuesta.status)) {
    let mensaje = `Error ${respuesta.status}`;
    try {
      const error = await respuesta.json();
      mensaje = error.mensaje || error.message || error.error || mensaje;
    } catch (_) {
      // Some server errors do not include a JSON body.
    }
    const excepcion = new Error(mensaje);
    excepcion.status = respuesta.status;
    throw excepcion;
  }
  return respuesta.status === 204 ? null : respuesta.json();
}

const enviar = (metodo, ruta, cuerpo) =>
  peticion(ruta, { method: metodo, body: JSON.stringify(cuerpo) });

const api = {
  aulas: {
    listar: (filtros = {}) => {
      const parametros = new URLSearchParams();
      if (filtros.capacidad !== "") parametros.set("capacidad", filtros.capacidad);
      if (filtros.ordenadores) parametros.set("ordenadores", "true");
      const query = parametros.toString();
      return peticion(`/aulas${query ? `?${query}` : ""}`);
    },
    crear: (aula) => enviar("POST", "/aulas", aula),
    actualizar: (id, aula) => enviar("PUT", `/aulas/${id}`, aula),
    borrar: (id) => peticion(`/aulas/${id}`, { method: "DELETE" }),
  },
  profesores: {
    listar: () => peticion("/profesores"),
    crear: (profesor) => enviar("POST", "/profesores", profesor),
    actualizar: (id, profesor) => enviar("PUT", `/profesores/${id}`, profesor),
    borrar: (id) => peticion(`/profesores/${id}`, { method: "DELETE" }),
  },
  franjas: {
    listar: () => peticion("/franjas-horarias"),
    crear: (franja) => enviar("POST", "/franjas-horarias", franja),
    actualizar: (id, franja) => enviar("PUT", `/franjas-horarias/${id}`, franja),
    borrar: (id) => peticion(`/franjas-horarias/${id}`, { method: "DELETE" }),
  },
  reservas: {
    listar: () => peticion("/reservas"),
    crear: (reserva) => enviar("POST", "/reservas", reserva),
    borrar: (id) => peticion(`/reservas/${id}`, { method: "DELETE" }),
  },
};
