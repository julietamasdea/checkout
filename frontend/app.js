const API_URL = "http://localhost:8080/checkout";

const cartEl = document.getElementById("cart");
const emptyEl = document.getElementById("empty");
const totalEl = document.getElementById("total");
const errorEl = document.getElementById("error");

/** @type {string[]} */
let items = [];

document.querySelectorAll("[data-sku]").forEach((button) => {
  button.addEventListener("click", () => {
    items.push(button.dataset.sku);
    render();
    refreshTotal();
  });
});

function removeAt(index) {
  items.splice(index, 1);
  render();
  refreshTotal();
}

function render() {
  cartEl.innerHTML = "";
  emptyEl.hidden = items.length > 0;

  items.forEach((sku, index) => {
    const li = document.createElement("li");
    li.innerHTML = `<span>${sku}</span>`;
    const removeBtn = document.createElement("button");
    removeBtn.type = "button";
    removeBtn.textContent = "sacar";
    removeBtn.addEventListener("click", () => removeAt(index));
    li.appendChild(removeBtn);
    cartEl.appendChild(li);
  });
}

async function refreshTotal() {
  errorEl.hidden = true;

  if (items.length === 0) {
    totalEl.textContent = "0 p";
    return;
  }

  try {
    const response = await fetch(API_URL, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ items }),
    });

    if (!response.ok) {
      throw new Error("Error " + response.status);
    }

    const data = await response.json();
    totalEl.textContent = data.total + " p";
  } catch (e) {
    totalEl.textContent = "—";
    errorEl.hidden = false;
    errorEl.textContent =
      "No se pudo calcular. ¿Está corriendo el API en :8080?";
  }
}

render();
