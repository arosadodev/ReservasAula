const API = "http://localhost:8080";
const tablaAulas = document.querySelector("#tabla-aulas");
const selectorAulas = document.querySelector("#reserva-aula");

async function peticion(ruta, opciones = {}) {
  const respuesta = await fetch(`${API}${ruta}`, {
    headers: { "Content-Type": "application/json" },
    ...opciones
  });
  const contenido = await respuesta.json().catch(() => ({}));
  if (!respuesta.ok) {
    const error = new Error(contenido.mensaje || "No se pudo completar la solicitud");
    error.estado = respuesta.status;
    throw error;
  }
  return contenido;
}

function mostrarAulas(aulas) {
  tablaAulas.innerHTML = aulas.map(aula => `
    <tr><td>${aula.nombre}</td><td>${aula.capacidad}</td>
    <td>${aula.ordenadores ? "Si" : "No"}</td></tr>`).join("");
  selectorAulas.innerHTML = aulas.map(aula =>
    `<option value="${aula.id}">${aula.nombre} (${aula.capacidad})</option>`).join("");
}

async function cargarAulas() {
  const params = new URLSearchParams();
  const capacidad = document.querySelector("#filtro-capacidad").value;
  if (capacidad) params.set("capacidad", capacidad);
  if (document.querySelector("#filtro-ordenadores").checked) params.set("ordenadores", "true");
  mostrarAulas(await peticion(`/aulas?${params}`));
}

document.querySelector("#filtros-aulas").addEventListener("submit", async evento => {
  evento.preventDefault();
  try { await cargarAulas(); } catch (error) { alert(error.message); }
});

document.querySelector("#limpiar-filtros").addEventListener("click", async () => {
  document.querySelector("#filtros-aulas").reset();
  try { await cargarAulas(); } catch (error) { alert(error.message); }
});

document.querySelector("#formulario-aula").addEventListener("submit", async evento => {
  evento.preventDefault();
  const formulario = new FormData(evento.target);
  const mensaje = document.querySelector("#mensaje-aula");
  try {
    await peticion("/aulas", { method: "POST", body: JSON.stringify({
      nombre: formulario.get("nombre"),
      capacidad: Number(formulario.get("capacidad")),
      ordenadores: formulario.get("ordenadores") === "on"
    }) });
    mensaje.textContent = "Aula creada correctamente.";
    mensaje.className = "mensaje exito";
    evento.target.reset();
    await cargarAulas();
  } catch (error) {
    mensaje.textContent = error.message;
    mensaje.className = "mensaje error";
  }
});

document.querySelector("#formulario-reserva").addEventListener("submit", async evento => {
  evento.preventDefault();
  const formulario = new FormData(evento.target);
  const mensaje = document.querySelector("#mensaje-reserva");
  try {
    await peticion("/reservas", { method: "POST", body: JSON.stringify({
      aula: { id: Number(selectorAulas.value) },
      profesor: { id: Number(formulario.get("profesorId")) },
      franjaHoraria: { id: Number(formulario.get("franjaHorariaId")) },
      fechaReserva: formulario.get("fechaReserva")
    }) });
    mensaje.textContent = "Reserva creada correctamente.";
    mensaje.className = "mensaje exito";
    evento.target.reset();
  } catch (error) {
    mensaje.textContent = error.estado === 400
      ? `Datos no validos: ${error.message}`
      : error.estado === 409
        ? `Conflicto de reserva: ${error.message}`
        : error.message;
    mensaje.className = "mensaje error";
  }
});

cargarAulas().catch(error => {
  document.querySelector("#mensaje-aula").textContent = error.message;
});
