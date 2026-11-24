// AI phone receptionist backend for a barber shop.
// The voice AI (Vapi) calls these tools during a phone call:
//   check_availability  -> free times for a service on a day
//   book_appointment    -> saves the booking
// The owner opens /bookings in a browser to see the diary.
// No packages needed: run with `node server.js`.

const http = require("http");
const fs = require("fs");
const path = require("path");

const PORT = process.env.PORT || 3000;
const SECRET = process.env.TOOL_SECRET || ""; // optional: must match the secret set in Vapi
const DATA = path.join(__dirname, "bookings.json");
const TZ = "Australia/Sydney";

const SHOP = {
  name: "Foreshore Fade Co.",
  services: {
    "mens haircut": { mins: 30, price: 40 },
    "skin fade": { mins: 45, price: 50 },
    "beard trim": { mins: 20, price: 25 },
    "cut and beard": { mins: 60, price: 60 },
    "kids cut": { mins: 30, price: 30 },
  },
  // day of week (0 = Sunday) -> [open hour, close hour]
  hours: { 2: [9, 18], 3: [9, 18], 4: [9, 20], 5: [9, 18], 6: [8, 15] },
};

function load() {
  try { return JSON.parse(fs.readFileSync(DATA, "utf8")); } catch { return []; }
}
function save(list) { fs.writeFileSync(DATA, JSON.stringify(list, null, 2)); }

function findService(name = "") {
  const n = name.toLowerCase();
  if (SHOP.services[n]) return [n, SHOP.services[n]];
  if (/kid|child/.test(n)) return ["kids cut", SHOP.services["kids cut"]];
  if (/beard/.test(n) && /cut|hair/.test(n)) return ["cut and beard", SHOP.services["cut and beard"]];
  if (/fade/.test(n)) return ["skin fade", SHOP.services["skin fade"]];
  if (/beard/.test(n)) return ["beard trim", SHOP.services["beard trim"]];
  if (/cut|hair/.test(n)) return ["mens haircut", SHOP.services["mens haircut"]];
  return [null, null];
}

// "HH:MM" <-> minutes since midnight
const toMin = (t) => { const [h, m] = t.split(":").map(Number); return h * 60 + (m || 0); };
const toHHMM = (m) => `${String(Math.floor(m / 60)).padStart(2, "0")}:${String(m % 60).padStart(2, "0")}`;
const say = (m) => { const h = Math.floor(m / 60), mm = m % 60; return `${((h + 11) % 12) + 1}${mm ? ":" + String(mm).padStart(2, "0") : ""}${h < 12 ? "am" : "pm"}`; };

function nowInShop() {
  const p = Object.fromEntries(new Intl.DateTimeFormat("en-CA", { timeZone: TZ, year: "numeric", month: "2-digit", day: "2-digit", hour: "2-digit", minute: "2-digit", hourCycle: "h23" })
    .formatToParts(new Date()).map((x) => [x.type, x.value]));
  return { date: `${p.year}-${p.month}-${p.day}`, mins: Number(p.hour) * 60 + Number(p.minute) };
}

function freeSlots(date, mins) {
  const dow = new Date(date + "T12:00:00Z").getUTCDay();
  const hrs = SHOP.hours[dow];
  if (!hrs) return null; // closed
  const now = nowInShop();
  if (date < now.date) return [];
  const taken = load().filter((b) => b.date === date).map((b) => [toMin(b.time), toMin(b.time) + b.mins]);
  const out = [];
  for (let t = hrs[0] * 60; t + mins <= hrs[1] * 60; t += 30) {
    if (date === now.date && t <= now.mins + 15) continue;
    if (taken.every(([a, b]) => t + mins <= a || t >= b)) out.push(t);
  }
  return out;
}

const tools = {
  check_availability({ service, date }) {
    const [svcName, svc] = findService(service);
    if (!svc) return `We don't offer "${service}". Services are: ${Object.keys(SHOP.services).join(", ")}.`;
    if (!/^\d{4}-\d{2}-\d{2}$/.test(date || "")) return "Please give the date as YYYY-MM-DD.";
    const slots = freeSlots(date, svc.mins);
    if (slots === null) return "The shop is closed that day. Open Tuesday to Saturday.";
    if (!slots.length) return "That day is fully booked. Offer another day.";
    return `Free times for a ${svcName} on ${date}: ${slots.map(say).join(", ")}.`;
  },
  book_appointment({ service, date, time, name, phone }) {
    const [svcName, svc] = findService(service);
    if (!svc) return `Unknown service "${service}".`;
    if (!date || !time || !name) return "Need service, date, time (HH:MM, 24-hour) and the customer's name.";
    const slots = freeSlots(date, svc.mins) || [];
    const t = toMin(time);
    if (!slots.includes(t)) return `Sorry, ${say(t)} on ${date} isn't free any more. Check availability again.`;
    const list = load();
    list.push({ date, time: toHHMM(t), mins: svc.mins, service: svcName, price: svc.price, name, phone: phone || "", via: "AI phone", created: new Date().toISOString() });
    save(list);
    return `Booked: ${svcName} for ${name} on ${date} at ${say(t)}. Price $${svc.price}.`;
  },
};

function diaryPage() {
  const rows = load().sort((a, b) => (a.date + a.time).localeCompare(b.date + b.time))
    .map((b) => `<tr><td>${b.date}</td><td>${say(toMin(b.time))}</td><td>${esc(b.service)}</td><td>${esc(b.name)}</td><td>${esc(b.phone)}</td><td>${esc(b.via)}</td></tr>`).join("");
  return `<!doctype html><meta charset="utf-8"><meta name="viewport" content="width=device-width"><title>${SHOP.name} diary</title>
<style>body{font-family:system-ui;margin:24px;background:#f3f4f2;color:#16181a}table{border-collapse:collapse;width:100%;background:#fff}td,th{padding:8px 12px;border-bottom:1px solid #ddd;text-align:left}</style>
<h1>${SHOP.name} — bookings</h1><table><tr><th>Date</th><th>Time</th><th>Service</th><th>Name</th><th>Phone</th><th>Via</th></tr>${rows || '<tr><td colspan="6">No bookings yet.</td></tr>'}</table>`;
}
const esc = (s) => String(s).replace(/[&<>"]/g, (c) => ({ "&": "&amp;", "<": "&lt;", ">": "&gt;", '"': "&quot;" })[c]);

http.createServer((req, res) => {
  if (req.method === "GET" && req.url === "/bookings") { res.writeHead(200, { "content-type": "text/html" }); return res.end(diaryPage()); }
  if (req.method === "GET" && req.url === "/") { res.writeHead(200); return res.end("AI receptionist is running."); }
  if (req.method === "POST" && req.url === "/vapi/tools") {
    if (SECRET && req.headers["x-vapi-secret"] !== SECRET) { res.writeHead(401); return res.end("bad secret"); }
    let body = "";
    req.on("data", (c) => (body += c));
    req.on("end", () => {
      let results = [];
      try {
        const calls = JSON.parse(body).message?.toolCallList || [];
        results = calls.map((c) => {
          let args = c.function?.arguments || {};
          if (typeof args === "string") args = JSON.parse(args);
          const fn = tools[c.function?.name];
          return { toolCallId: c.id, result: fn ? fn(args) : `Unknown tool ${c.function?.name}` };
        });
      } catch (e) { results = [{ toolCallId: "error", result: "Sorry, something went wrong. Take a message instead." }]; }
      res.writeHead(200, { "content-type": "application/json" });
      res.end(JSON.stringify({ results }));
    });
    return;
  }
  res.writeHead(404); res.end("not found");
}).listen(PORT, () => console.log(`Receptionist backend on port ${PORT}`));
