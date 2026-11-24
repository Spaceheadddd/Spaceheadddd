# AI Phone Receptionist (barber demo)

An AI that answers the phone for a barber shop, answers questions and books appointments.

**How it works**
1. A customer calls (or, for the demo, clicks "Talk" in your browser).
2. **Vapi** handles the voice: it listens, thinks with AI, and talks back.
3. When it needs to check times or book, Vapi asks **this small server** (`server.js`).
4. The owner sees bookings at `https://YOUR-SERVER-ADDRESS/bookings`.

## Files
- `server.js`: the booking server. No packages needed.
- `assistant-prompt.md`: what the AI knows and how it behaves. Paste it into Vapi.
- `tools.json`: the two actions the AI can take (check times, book).

## Setup: about 30 minutes, $0

### 1. Put the server online (free)
Use **Render** (render.com), which has a free plan:
1. Sign up with GitHub.
2. New → Web Service → choose this repo.
3. Root directory: `ai-receptionist` · Start command: `node server.js`
4. Add an environment variable `TOOL_SECRET`, set to any long random word.
5. Deploy. You get an address like `https://foreshore-xxxx.onrender.com`.

Note: on the free plan the server sleeps when it isn't used, and bookings reset when it restarts. That's fine for a demo. For a paying customer, move to a $5–10 a month server.

### 2. Make the AI voice (free trial credit)
1. Sign up at **vapi.ai**. New accounts get free credit.
2. Create Assistant → Blank.
3. **Model:** paste the system prompt from `assistant-prompt.md`. Paste the first message too.
4. **Voice:** pick a natural Australian-sounding voice.
5. **Tools:** create two "Function" tools, `check_availability` and `book_appointment`, using `tools.json`. Set each one's server URL to `https://YOUR-SERVER-ADDRESS/vapi/tools` and its secret to your `TOOL_SECRET`.
6. Press **Talk** and book yourself a skin fade.

### 3. Record the demo
Record your screen while you call and book. Then open `/bookings` to show it appeared.

### 4. When you get a paying customer
- Buy an Australian phone number (through Vapi or Twilio, roughly $5–15 a month) and connect it to the assistant.
- The shop sets their mobile to **forward calls to that number when they don't answer**. Their normal number stays the same.
- Change the shop facts in `assistant-prompt.md` and the services and hours in `server.js`.

## Run locally
```
node server.js
# open http://localhost:3000/bookings
```
