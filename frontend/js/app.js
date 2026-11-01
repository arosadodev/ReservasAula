const $ = (id) => document.getElementById(id);
let aulas = [], profesores = [], franjas = [];

function el(etiqueta, props = {}, ...hijos) {
  const nodo = Object.assign(document.createElement(etiqueta), props);
  nodo.append(...hijos);
  return nodo;
}

function mostrarMensaje(texto, error = false) {
  const mensaje = $("mensaje");
  mensaje.textContent = texto;
  mensaje.className = `mensaje${error ? " error" : ""}`;
  clearTimeout(mostrarMensaje.temporizador);
  mostrarMensaje.temporizador = setTimeout(() => mensaje.classList.add("oculto"), 5000);
}

async function ejecutar(accion, correcto) {
  try { await accion(); if (correcto) mostrarMensaje(correcto); }
  catch (error) { mostrarMensaje(error.message, true); }
}

function filaVacia(columnas, texto) {
  return el("tr", {}, el("td", { colSpan: columnas, className: "vacio", textContent: texto }));
}

function rellenarSelect(select, elementos, texto) {
  select.replaceChildren(el("option", { value: "", textContent: "-- Selecciona --" }),
    ...elementos.map((item) => el("option", { value: item.id, textContent: texto(item) })));
}

function abrirFormulario(titulo, campos, valores, guardar) {
  $("dialogo-titulo").textContent = titulo;
  const contenedor = $("dialogo-campos");
  contenedor.replaceChildren();
  for (const campo of campos) {
    const control = el("input", { ...campo, value: valores[campo.name] ?? "" });
    if (campo.type === "checkbox") {
      control.checked = Boolean(valores[campo.name]);
      control.removeAttribute("value");
    }
    delete control.label;
    contenedor.append(el("label", {}, campo.label, control));
  }
  $("form-dialogo").onsubmit = (evento) => {
    evento.preventDefault();
    const datos = Object.fromEntries(new FormData(evento.target));
    $("dialogo").close();
    guardar(datos);
  };
  $("dialogo").showModal();
}

function pintarAulas(lista) {
  const cuerpo = $("tabla-aulas"); cuerpo.replaceChildren();
  if (!lista.length) cuerpo.append(filaVacia(4, "No hay aulas"));
  lista.forEach((aula) => cuerpo.append(el("tr", {},
    el("td", { textContent: aula.nombre }), el("td", { textContent: aula.capacidad }),
    el("td", { textContent: aula.ordenadores ? "Sí" : "No" }),
    acciones(() => editarAula(aula), () => borrar("aula", aula, api.aulas.borrar, cargarAulas)))));
}

async function cargarAulas() {
  const f = $("filtro-aulas");
  aulas = await api.aulas.listar({ capacidad: f.capacidad.value, ordenadores: f.ordenadores.checked });
  pintarAulas(aulas);
}

const CAMPOS_AULA = [
  { name: "nombre", label: "Nombre", required: true },
  { name: "capacidad", label: "Capacidad", type: "number", min: "1", required: true },
  { name: "ordenadores", label: "¿Tiene ordenadores?", type: "checkbox" },
];
function datosAula(d) { return { nombre: d.nombre, capacidad: Number(d.capacidad), ordenadores: d.ordenadores === "on" }; }
function editarAula(aula) { abrirFormulario("Editar aula", CAMPOS_AULA, aula, (d) => ejecutar(async () => { await api.aulas.actualizar(aula.id, datosAula(d)); await cargarAulas(); }, "Aula actualizada")); }
$("nuevo-aula").onclick = () => abrirFormulario("Nueva aula", CAMPOS_AULA, {}, (d) => ejecutar(async () => { await api.aulas.crear(datosAula(d)); await cargarAulas(); }, "Aula creada"));

function pintarProfesores(lista) {
  const cuerpo = $("tabla-profesores"); cuerpo.replaceChildren();
  if (!lista.length) cuerpo.append(filaVacia(4, "No hay profesores"));
  lista.forEach((p) => cuerpo.append(el("tr", {},
    el("td", { textContent: p.nombre }), el("td", { textContent: p.apellidos }), el("td", { textContent: p.email || "" }),
    acciones(() => editarProfesor(p), () => borrar("profesor", p, api.profesores.borrar, cargarProfesores)))));
}
async function cargarProfesores() { profesores = await api.profesores.listar(); pintarProfesores(profesores); }
const CAMPOS_PROFESOR = [
  { name: "nombre", label: "Nombre", required: true },
  { name: "apellidos", label: "Apellidos", required: true },
  { name: "email", label: "Email", type: "email", required: true },
];
function editarProfesor(p) { abrirFormulario("Editar profesor", CAMPOS_PROFESOR, p, (d) => ejecutar(async () => { await api.profesores.actualizar(p.id, d); await cargarProfesores(); }, "Profesor actualizado")); }
$("nuevo-profesor").onclick = () => abrirFormulario("Nuevo profesor", CAMPOS_PROFESOR, {}, (d) => ejecutar(async () => { await api.profesores.crear(d); await cargarProfesores(); }, "Profesor creado"));

function pintarFranjas(lista) {
  const cuerpo = $("tabla-franjas"); cuerpo.replaceChildren();
  if (!lista.length) cuerpo.append(filaVacia(3, "No hay franjas horarias"));
  lista.forEach((f) => cuerpo.append(el("tr", {},
    el("td", { textContent: f.horaInicio }), el("td", { textContent: f.horaFin }),
    acciones(() => editarFranja(f), () => borrar("franja", f, api.franjas.borrar, cargarFranjas)))));
}
async function cargarFranjas() { franjas = await api.franjas.listar(); pintarFranjas(franjas); }
const CAMPOS_FRANJA = [
  { name: "horaInicio", label: "Hora de inicio", type: "time", required: true },
  { name: "horaFin", label: "Hora de fin", type: "time", required: true },
];
function editarFranja(f) { abrirFormulario("Editar franja horaria", CAMPOS_FRANJA, f, (d) => ejecutar(async () => { await api.franjas.actualizar(f.id, d); await cargarFranjas(); }, "Franja actualizada")); }
$("nueva-franja").onclick = () => abrirFormulario("Nueva franja horaria", CAMPOS_FRANJA, {}, (d) => ejecutar(async () => { await api.franjas.crear(d); await cargarFranjas(); }, "Franja creada"));

function nombreAula(a) { return `${a.nombre} (${a.capacidad} plazas)`; }
function nombreProfesor(p) { return `${p.nombre} ${p.apellidos}`; }
function nombreFranja(f) { return `${f.horaInicio} - ${f.horaFin}`; }
function fechaLocalISO() {
  const ahora = new Date();
  const desfase = ahora.getTimezoneOffset() * 60000;
  return new Date(ahora.getTime() - desfase).toISOString().slice(0, 10);
}
function acciones(editar, eliminar) {
  return el("td", { className: "acciones" }, el("button", { textContent: "Editar", onclick: editar }), el("button", { textContent: "Borrar", className: "peligro", onclick: eliminar }));
}
function borrar(tipo, item, metodo, recargar) {
  if (!confirm(`¿Borrar ${tipo} ${item.nombre || ""}?`)) return;
  ejecutar(async () => { await metodo(item.id); await recargar(); }, `${tipo[0].toUpperCase() + tipo.slice(1)} eliminado`);
}

async function cargarReservas() {
  await Promise.all([cargarAulas(), cargarProfesores(), cargarFranjas()]);
  const formulario = $("form-reserva");
  rellenarSelect(formulario.aula, aulas, nombreAula);
  rellenarSelect(formulario.profesor, profesores, nombreProfesor);
  rellenarSelect(formulario.franjaHoraria, franjas, nombreFranja);
  formulario.fechaReserva.min = fechaLocalISO();
  const reservas = await api.reservas.listar();
  const cuerpo = $("tabla-reservas"); cuerpo.replaceChildren();
  if (!reservas.length) cuerpo.append(filaVacia(5, "No hay reservas"));
  reservas.forEach((r) => cuerpo.append(el("tr", {},
    el("td", { textContent: r.fechaReserva }), el("td", { textContent: r.aula ? nombreAula(r.aula) : "" }),
    el("td", { textContent: r.profesor ? nombreProfesor(r.profesor) : "" }),
    el("td", { textContent: r.franjaHoraria ? nombreFranja(r.franjaHoraria) : "" }),
    el("td", { className: "acciones" }, el("button", { textContent: "Cancelar", className: "peligro", onclick: () => cancelarReserva(r) })))));
}
function cancelarReserva(r) {
  if (!confirm("¿Cancelar esta reserva?")) return;
  ejecutar(async () => { await api.reservas.borrar(r.id); await cargarReservas(); }, "Reserva cancelada");
}

document.querySelectorAll(".tab").forEach((boton) => boton.onclick = () => {
  document.querySelectorAll(".tab").forEach((b) => b.classList.toggle("active", b === boton));
  document.querySelectorAll(".panel").forEach((p) => p.classList.toggle("oculto", p.id !== boton.dataset.tab));
  if (boton.dataset.tab === "reservas") ejecutar(cargarReservas);
});
$("dialogo-cancelar").onclick = () => $("dialogo").close();
$("filtro-aulas").onsubmit = (e) => { e.preventDefault(); ejecutar(cargarAulas); };
$("limpiar-filtro").onclick = () => { $("filtro-aulas").reset(); ejecutar(cargarAulas); };
$("form-reserva").onsubmit = (e) => {
  e.preventDefault();
  const f = e.target;
  ejecutar(async () => {
    await api.reservas.crear({ aula: { id: Number(f.aula.value) }, profesor: { id: Number(f.profesor.value) }, franjaHoraria: { id: Number(f.franjaHoraria.value) }, fechaReserva: f.fechaReserva.value });
    f.reset();
    await cargarReservas();
  }, "Reserva creada");
};
ejecutar(cargarAulas);
