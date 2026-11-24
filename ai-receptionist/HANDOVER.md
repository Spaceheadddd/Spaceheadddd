# Project Handover: AI Receptionist & Managed Hosting Business

| | |
|---|---|
| **Document type** | Project handover. Written so a person or another AI can pick this up cold. |
| **Version** | 2.0 (replaces v1, which was too focused on the phone product and on barbers) |
| **Date** | 24 November 2026 |
| **Location of business** | Newcastle, NSW, Australia |
| **Project status** | Discovery done · direction agreed · two demos built · lead system designed, not built |
| **Code location** | `ai-receptionist/` folder, branch `ccr-dc5207a9-w25myz` |

> **Read this first (for the next AI):** the owner of this project wants **plain English**, practical steps and honest advice. The hard rule is **$0 upfront**: nothing gets paid for until a customer has paid. Read Sections 1–4 before suggesting anything, because they explain *why* the project looks the way it does.

---

## Contents
1. Executive summary
2. Origin: what the owner came for
3. Discussion record: what was asked, suggested and decided
4. Overall direction
5. The offer: product, services and add-ons
6. The server side: white label, hosting and infrastructure
7. Target markets: verticals and use cases
8. Lead generation system (automated)
9. Lead tracker and automated outreach
10. Free-first resources
11. What has been built so far
12. Plan and roadmap
13. Scope
14. Risks, compliance and open questions
15. Rules and next actions for whoever takes over

---

## 1. Executive summary

The owner has **no capital** but strong technical skills (coding, AI, Linux, Docker, security). They came looking for a low-cost business they could start without upfront cost, first thinking of **renting out servers**.

Discussion showed that renting raw servers is a weak business: big providers win on price and margins are thin. The agreed direction keeps the server idea but changes **what is sold on top of the servers**:

> **A white-label, managed AI receptionist service for local businesses.** It's hosted on our own (initially free) servers and sold under our own brand as a monthly service. It answers customer enquiries on the website and in messages 24/7, captures leads and books appointments. **Phone answering is an add-on**, not the core product.

The business runs on three parts:

| Part | What it is | Why it matters |
|---|---|---|
| **Infrastructure** | Our own multi-customer server(s), starting on free tiers | This is the "rent out servers" idea in a form that earns proper margins |
| **Product** | AI receptionist (website chat, messaging, bookings, lead capture) plus add-ons | What customers actually pay for |
| **Sales engine** | Automated lead finding, business profiling, tracker and email outreach | Finding customers is the hardest part, so we automate it |

**Money model:** setup fee paid upfront → that pays for any infrastructure → monthly retainer is mostly profit. Target: **10–15 clients ≈ $1,000–2,000 AUD a month** in recurring income.

---

## 2. Origin: what the owner came for

The opening message, quoted exactly:

> "I'm looking for a low cost business model where I can rent out servers or something like that without any upfront cost.
>
> I'm broke atm and I can build systems - meaning I'm technology driven via coding and ai.
>
> What's my best bet ?"

**What they were really asking for:**

| Need | Meaning |
|---|---|
| A business **model**, not a one-off job | Recurring income |
| **Low or no upfront cost** | They can't buy hardware, software or ads |
| **Servers or "something like that"** | They're drawn to infrastructure: hosting, reselling, white label |
| **Use their skills** | Coding and AI as the advantage instead of money |
| **"Best bet"** | A clear recommendation, not a list of options |

They later clarified: by "rent out servers" they meant **wholesale, reselling or white-label** arrangements, buying capacity or a platform and reselling under their own name.

---

## 3. Discussion record: what was asked, suggested and decided

### 3.1 Timeline

| # | Owner said or asked | Suggested | Outcome |
|---|---|---|---|
| 1 | Low-cost model like renting servers, no upfront cost, "what's my best bet?" | Don't sell raw servers. Sell **what runs on them**: managed hosting plus an app plus support. Avoid buying GPUs or hardware and generic hosting resale. | Direction started |
| 2 | "Speak plain English. I meant wholesale or white label." | Explained white label. Compared plain hosting resale ($5–15 a client, high churn) with **hosting + app + maintenance** ($50–300 a client, low churn). | Owner liked the bundled model |
| 3 | "I've no capital, just time and skills. So server + app + maintenance?" | Confirmed. **Customer pays first, then buy what's needed.** Demos built on free tiers. | **$0-upfront rule agreed** |
| 4 | "Let's plan. Do I need a website?" | Not at first, since the demo sells it. Later, a one-page site on free hosting. 30-day plan given. | Plan accepted |
| 5 | Newcastle NSW, public transport only, bookings-oriented | Booking businesses, outreach mostly online, visits along transport routes. Pricing in AUD. | **Bookings focus agreed** |
| 6 | ABN active. "Do a barber." | Built **Demo 1**: a scripted chat booking assistant for a made-up barber. | Barber used as the **first demo example**, not the only market |
| 7 | "Is this just a chatbot? What changes with AI? What if they don't do bookings?" | Honest answer: Demo 1 is scripted. Real AI understands any wording and works across channels. Phone answering is a strong selling point. Non-booking businesses can use enquiries, waitlists and reviews. | |
| 8 | "Let's do that." | Built **Demo 2**: the booking server and voice-AI setup (Vapi). | **Phone = add-on service**, built because it's a strong upsell |
| 9 | "To add a chatbot to their site, don't I need their code?" | No. One pasted line in Wix, Squarespace, WordPress or Shopify settings. Or use a booking link, a QR code or social media messages. | Removes a worry about selling |
| 10 | Asked for a handover and code to run locally | v1 handover and zip delivered | |
| 11 | "Handover isn't up to the mark" | This v2: product centred on the receptionist, deeper look at servers, more markets, **automated lead system**, free resources | **Current document** |

### 3.2 Owner's concerns, and how they were resolved

| Concern | Resolution |
|---|---|
| No money | Free tiers for everything until the first payment. Setup fee is paid upfront. |
| No car | Online outreach first. Automated lead finding. In-person visits grouped along light rail, bus and train routes. |
| "Is it just a chatbot?" | The product sold is real AI. The scripted demo is only for showing the flow. |
| Businesses without bookings | Sell enquiry handling, lead capture, waitlists and review requests, or rank them lower. |
| Needing access to client code | Not needed. One embed line, or no website at all. |
| Competing with existing tools (Fresha, HotDoc, chat widgets) | **Profile what they already have** and sell what's missing (see Section 8). Connect to their tools instead of replacing them. |
| Previous handover too narrow | This version covers servers, multiple markets and the sales engine. |

---

## 4. Overall direction

```
 "Rent out servers"  ──▶  "White-label / resell infrastructure"  ──▶  "Sell a managed AI service that runs on our servers"
     (weak margin)              (still commodity)                        (high margin, sticky, uses owner's skills)
```

**Positioning:** *A local Newcastle tech partner who gives small businesses a 24/7 AI receptionist, fully set up and looked after for them.*

**Principles agreed:**
1. **$0 upfront.** Free tiers, and the customer pays before any spending.
2. **Sell outcomes, not tech.** "You stop losing enquiries", not "a GPT chatbot".
3. **One core product, many markets.** The same platform is reused across industries.
4. **Recurring revenue first.** Setup fee plus monthly retainer.
5. **Automate the boring parts,** above all finding and contacting leads.
6. **Complement, don't replace.** Work alongside the booking and chat tools businesses already use.

---

## 5. The offer: product, services and add-ons

### 5.1 Core product: AI Receptionist
| Capability | Description |
|---|---|
| Website chat | A chat bubble on the client's site (one line of code) |
| Answers questions | Trained on the business's own info: services, prices, hours, policies, location |
| Lead capture | Collects name, contact and need, and sends it to the owner instantly (email or SMS) |
| Bookings | Books into the client's booking system or calendar, or takes a booking request |
| After-hours cover | Works 24/7 |
| Hand-off to a human | Passes the conversation on when unsure |
| Monthly report | Number of enquiries, bookings and common questions |

### 5.2 Add-on services (upsells)
| Add-on | What it does | Fits best |
|---|---|---|
| **AI phone answering** | Answers missed or after-hours calls by voice and books or takes messages (Demo 2) | Clinics, trades, salons |
| Messaging channels | Same assistant on Facebook Messenger, Instagram DMs, SMS, WhatsApp | Salons, restaurants, gyms |
| Reminders and no-show reduction | SMS or email reminders before appointments | Clinics, salons |
| Review requests | Asks happy customers for a Google review after a visit | Everyone |
| Follow-up and reactivation | "It's been 6 weeks since your last cut or check-up" | Salons, dental, physio |
| Quote or intake forms | Collects job details and photos before a quote | Trades |
| Managed website | Simple site built and hosted by us | Businesses with no or bad websites |

### 5.3 Pricing (AUD, starting point to test)
| Package | Includes | Setup | Monthly |
|---|---|---|---|
| **Starter** | Website chat + FAQ + lead capture | $200–300 | $79–99 |
| **Booking** | Starter + bookings + reminders | $300–500 | $129–179 |
| **Full receptionist** | Booking + phone answering + messaging | $500–800 | $249–399 |
| Add-ons | Reviews, reactivation, managed website | — | +$29–99 each |

First 3 clients get about 50% off setup in exchange for a testimonial and case study.

---

## 6. The server side: white label, hosting and infrastructure

This is the part of the business that matches the owner's original "rent out servers" idea.

### 6.1 Three ways to run the service
| Option | How it works | Upfront | Margin | Control | Verdict |
|---|---|---|---|---|---|
| **A. Resell a white-label platform** (e.g. GoHighLevel agency plans or white-label chatbot platforms) | Pay a platform monthly, rebrand it, resell | Monthly fee (often $97+ USD) | Medium | Low | ❌ Costs money before the first client |
| **B. Build our own platform on free or cheap servers** | Our own code, multi-client, our brand | **$0** | **High** | Full | ✅ **Chosen.** Fits the owner's skills and the $0 rule |
| **C. Hybrid** | Our own platform, plus third-party pieces (voice from Vapi or Retell, AI from model APIs, email from Brevo or Resend) | $0 (free tiers and credits) | High | High | ✅ **What we actually do**: build the core, rent the hard parts per use |

### 6.2 Target architecture (multi-tenant, meaning one platform serving many clients)
```
                ┌─────────────── Our server (free tier → paid VPS later) ───────────────┐
Client site ──▶ │ chat widget (JS) ─▶ API ─▶ AI layer (prompt per client) ─▶ tools:     │
Messenger   ──▶ │                             │                      check times, book, │
Phone (Vapi)──▶ │                             └─▶ client config      capture lead,      │
                │                                 (services, hours,  notify owner       │
                │                                  FAQ, branding)                        │
                │ database (clients, leads, bookings, conversations)                     │
                │ owner dashboard  · admin dashboard (us)  · reports                     │
                └──────────────────────────────────────────────────────────────────────┘
```

### 6.3 Server growth path
| Stage | Clients | Hosting | Cost a month (approx.) |
|---|---|---|---|
| Demo and dry run | 0 | Oracle Cloud Always Free, Render free, or a local machine plus a tunnel | $0 |
| First clients | 1–5 | Same free tier, or a cheap VPS paid from setup fees | $0–10 |
| Growing | 5–30 | One VPS (Hetzner, DigitalOcean, Vultr, or an Australian host for lower latency) | $10–40 |
| Established | 30+ | 2+ servers, backups, monitoring | $50–150 |

One small server can host many clients because each one is just a configuration plus a share of the database. **This is where the server margin comes from.**

### 6.4 Infrastructure resale opportunities (later)
Once the platform exists, the same servers can carry:
- Managed websites for clients (hosting + updates)
- Managed self-hosted tools: n8n automations, uptime monitoring, analytics
- Business email setup and management

These are classic "rent out servers" products, sold bundled with the receptionist instead of on their own.

---

## 7. Target markets: verticals and use cases

### 7.1 Market ranking
| Rank | Vertical | Key pain | What we sell | Typical tools they already use | Notes |
|---|---|---|---|---|---|
| 1 | **Allied health clinics** (physio, chiro, podiatry, massage, osteo, psych) | Reception is overloaded, after-hours enquiries are lost | Chat + booking + phone add-on + reminders | Cliniko, Nookal, Halaxy, HotDoc | High value per booking. Owners pay for admin relief. |
| 2 | **Dental clinics** | Missed calls mean lost new patients | Chat + new-patient capture + phone | HotDoc, Dental4Windows, Centaur | High value. Some already have tools, so profile carefully. |
| 3 | **Hair, beauty, barber, nails, lashes** | Owner is busy with clients | Chat + bookings + messaging + reviews | Fresha, Timely, Square, Kitomba | Volume market. The demo already exists (barber). |
| 4 | **Trades** (plumbers, electricians, HVAC) | On the tools, can't answer, lose jobs | Chat + quote intake + phone | ServiceM8, Tradify, none | Very strong phone use case. Jobs are high value. |
| 5 | **Vets and pet groomers** | Phone-heavy, repetitive questions | Chat + bookings + FAQ | VetPortal, Gingr, none | |
| 6 | **Gyms, studios, PTs** | Trial sign-ups and class questions | Chat + trial booking + follow-up | Mindbody, Glofox | |
| 7 | **Real estate and property management** | Rental and repair enquiries | Chat + enquiry triage | Rex, PropertyMe | Larger firms, longer sales cycle |
| 8 | **Restaurants and cafes** | Bookings and functions enquiries | Chat + reservations | OpenTable, NowBookIt | Lower budget |
| 9 | **Driving schools, tutors, mechanics** | Calls missed during lessons or jobs | Chat + booking + phone | Varied | Owner-operators |

**Recommendation:** start with **allied health clinics** (highest value and clearest pain) and **salons/barbers** (fast sales, demo ready). Test both, then double down on whichever converts.

### 7.2 Example scenarios
| Scenario | Without us | With us |
|---|---|---|
| Physio clinic, 8pm: "Do you treat sciatica? Can I come Saturday?" | Message unseen until morning, so the patient books elsewhere | AI answers, offers a Saturday slot, books it or captures the details. The owner sees it in the morning. |
| Plumber on a job, phone rings | Missed call, so the customer calls the next plumber | AI phone add-on takes the job details and urgency, and texts the plumber |
| Dental clinic, new patient asks about price and health funds | Receptionist is busy, so the patient is put on hold | AI answers common questions, collects details and books the first visit |
| Salon client asks on Instagram "any spots Friday?" | Owner replies hours later | AI replies instantly with times |

### 7.3 Clinic-specific rules
- The AI must **never give medical advice or triage symptoms**. It handles admin only and directs emergencies to 000.
- **Health information is sensitive** under Australian privacy law. Collect only what's needed (name, contact, preferred time, reason in general terms). Don't store clinical details.
- Usually connect to their existing practice system (Cliniko, Nookal and similar have APIs) rather than replacing it.

---

## 8. Lead generation system (automated)

**Goal:** a script-driven pipeline that finds local businesses, profiles what they already have, scores how likely they are to buy, and finds a public contact. The owner has no car and limited time, so automation does the legwork.

### 8.1 Pipeline overview
```
[1 Collect] ──▶ [2 Enrich website] ──▶ [3 Detect tools] ──▶ [4 Profile & score] ──▶ [5 Find contact] ──▶ [6 Tracker] ──▶ [7 Outreach]
 maps/APIs        fetch homepage         chat? booking?       gaps, size, fit         public emails         sheet/DB          emails + demos
```

### 8.2 Stage 1: Collect businesses
| Source | What it gives | Cost | Notes |
|---|---|---|---|
| **OpenStreetMap (Overpass API)** | Name, category, address, sometimes website and phone | Free | ✅ Best free start. Fair-use limits. |
| **Google Places API** | Name, website, phone, rating, review count, hours | Free monthly allowance, then paid | Use the **official API**. Scraping Google Maps directly breaks Google's terms. Check current free limits. |
| **ABN Lookup web services** (abr.business.gov.au) | Business name, ABN status, entity type | Free (register for a GUID) | Confirms the business is active |
| **Industry directories** (Healthdirect, HotDoc and Fresha listings, Yellow Pages, True Local) | Names, websites, categories | Free to browse | Check each site's terms before automating. Use manually or lightly. |
| **Manual input** | Businesses seen in person or by referral | Free | Always allowed |

Search radius: Newcastle LGA, then Lake Macquarie, then Maitland and Port Stephens.

### 8.3 Stage 2–3: Enrich the website and detect existing tools
For each business with a website, fetch the homepage (and `/contact`, `/book`) and look for known code signatures:

| Category | Tools to detect (signature in page HTML or scripts) |
|---|---|
| **Chat widgets** | Intercom, Tidio, Drift, Crisp, Tawk.to, LiveChat, HubSpot chat, Zendesk, Facebook chat plugin, Freshchat, Podium, Birdeye |
| **AI chat or voice** | Chatbase, Voiceflow, Botpress, custom GPT widgets, mentions of "AI assistant" |
| **Booking (health)** | HotDoc, HealthEngine, Cliniko, Nookal, Halaxy, Jane |
| **Booking (beauty and general)** | Fresha, Timely, Square Appointments, Kitomba, Setmore, Calendly, Acuity, SimplyBook |
| **Trades** | ServiceM8, Tradify booking widgets |
| **Website platform** | Wix, Squarespace, WordPress, Shopify, Webflow (tells us how easy the install is) |
| **Other signals** | Contact form only, no online booking, mobile-friendly or not, SSL, social links, "call us" as the only option |

### 8.4 Stage 4: Profile and score
**Competitor profile.** If they already have a chat or AI tool:

| Question | Why |
|---|---|
| Which tool is it? | Know what we're up against |
| Is it a live-chat widget (needs a human) or AI? | Live chat with no staff after hours is a gap we fill |
| Can it book, or just collect messages? | Booking is a clear upgrade |
| Does it know their services and prices? | Generic bots perform badly |
| After-hours and phone cover? | Phone add-on opportunity |
| **Our pitch angle** | "Keep your X. We add Y." |

**Lead score (0–100), a starting formula:**
| Signal | Points |
|---|---|
| Takes bookings (or vertical rank 1–4) | +25 |
| **No chat or AI assistant on the site** | +20 |
| Has a chat widget that is human-only or basic | +10 |
| Has a booking system but no assistant | +10 |
| Phone or contact form is the only contact option | +10 |
| 20–300 Google reviews (established, but not a big chain) | +10 |
| Independent business, not a franchise | +10 |
| Public email found | +5 |
| Already has an advanced AI receptionist | −30 |
| No website and no socials | −10 (still contact by phone or a visit) |

### 8.5 Stage 5: Find contact details (the legal way)
| Method | Allowed? |
|---|---|
| Email published on their website (mailto links, the contact page) | ✅ Yes |
| Email on their Google, Facebook or directory listing | ✅ Yes |
| Owner name from the website "About" page | ✅ Yes, for personalising |
| Free email-finder tiers (e.g. Hunter.io, about 25 searches a month) | ⚠️ Use for high-score leads only |
| Guessing addresses (firstname@...) or buying email lists | ❌ No. Spam risk, and against the Spam Act (Section 14) |

### 8.6 Lead record (data fields)
`id, business_name, vertical, suburb, address, phone, website, public_email, contact_name, source, google_rating, review_count, site_platform, chat_tool, ai_tool, booking_tool, has_online_booking, gaps, pitch_angle, score, status, last_contacted, next_action, next_action_date, notes, demo_url, unsubscribed`

---

## 9. Lead tracker and automated outreach

### 9.1 Tracker
| Option | Cost | Pros | Cons |
|---|---|---|---|
| **Google Sheets** (start here) | Free | Easy, works on a phone, scripts can update it | Gets messy at scale |
| Airtable or Notion | Free tier | Nicer views and kanban boards | Free-tier limits |
| **Own dashboard** (SQLite + small web app on our server) | Free | Full control, ties into outreach | Takes time to build. Do it later. |

### 9.2 Pipeline statuses
| Status | Meaning | Next automatic action |
|---|---|---|
| `new` | Collected, not yet checked | Run enrichment |
| `profiled` | Tools detected, score set | If score ≥ 60, queue for outreach |
| `queued` | Ready to email | Send email 1 |
| `contacted` | Email 1 sent | Follow-up in 3–4 days if no reply |
| `followed_up` | Email 2 sent | Final email in 7 days |
| `replied` | They answered | **Human takes over** |
| `demo_booked` | Demo call or visit scheduled | Reminder the day before |
| `demo_done` | Demo given | Send proposal |
| `won` | Paid setup fee | Start onboarding |
| `lost` / `not_now` | Not interested | Recheck in 3–6 months |
| `unsubscribed` | Asked to stop | **Never contact again** |

### 9.3 Email sequence (personalised from the profile)
| # | When | Content |
|---|---|---|
| 1 | Day 0 | Personal note mentioning something specific ("I noticed your site only takes bookings by phone"), a one-line benefit, a link to **a demo built for their business**, and an unsubscribe line |
| 2 | Day 3–4 | Short follow-up and one example scenario for their industry |
| 3 | Day 10 | Last note: "Happy to leave it here. Reply 'demo' anytime." |

Limits: start with **20–30 emails a day** from a proper domain, so we look trustworthy and stay out of spam folders.

### 9.4 Personalised demos (strong differentiator)
Automatically generate a demo page for each high-scoring lead using their **name, services, hours and suburb** taken from their website. For example: "Here's what your assistant would look like: demo.ourbrand.com/hamilton-physio". This builds on `chat-demo.html`.

### 9.5 Outreach tools
| Tool | Free tier (check current limits) | Use |
|---|---|---|
| Brevo | About 300 emails a day | Sequences and tracking |
| Resend | About 3,000 emails a month | Sending from scripts |
| Gmail plus a mail-merge script | Small daily limits | Very early testing |
| n8n (self-hosted on our server) | Free | Connects the pipeline: sheet → email → status updates |

---

## 10. Free-first resources

**Why free first:** the owner has no money to invest. Everything until the first client is built on free tiers, trial credits or open source. Paid services are switched on **only after a client has paid**, and their cost is built into the monthly fee. *Free-tier limits change often, so confirm current terms before relying on them.*

### 10.1 Servers and hosting
| Resource | Free offer (approx.) | Use |
|---|---|---|
| **Oracle Cloud Always Free** | ARM VMs (up to about 4 CPU / 24 GB RAM total) | ✅ Main free server for the platform |
| Google Cloud free tier | 1 small e2-micro VM (US regions) | Backup or small jobs |
| Render | Free web services (sleep when idle) | Demo hosting |
| Cloudflare Pages / Workers | Generous free tier | Chat widget JS, demo pages, landing page |
| Vercel / Netlify | Free hobby tier | Landing page and demos |
| GitHub | Free repos, Actions, Pages | Code, scheduled lead-collection scripts |
| Local computer + tunnel (Cloudflare Tunnel, ngrok, localtunnel) | Free | Development and testing |

### 10.2 Database and storage
| Resource | Free offer | Use |
|---|---|---|
| SQLite | Free, built in | Simplest start, on the server itself |
| Supabase | Free Postgres + auth | Clients, leads, bookings |
| Neon | Free Postgres | Alternative |
| Google Sheets | Free | Lead tracker |

### 10.3 AI and voice
| Resource | Free offer | Use |
|---|---|---|
| Google Gemini API | Free tier with rate limits | Demos and low volume |
| Groq | Free tier for open models | Fast, cheap replies |
| OpenRouter | Some free models | Testing |
| Anthropic Claude / OpenAI APIs | Pay per use (cents per conversation) | Production quality once clients pay |
| **Vapi / Retell** | Free starting credit | Phone answering add-on (Demo 2) |
| Twilio | Trial credit | SMS and phone numbers once clients pay |

### 10.4 Leads, email and automation
| Resource | Free offer | Use |
|---|---|---|
| OpenStreetMap Overpass API | Free | Business lists |
| Google Places API | Monthly free allowance | Websites, ratings, reviews |
| ABN Lookup web services | Free | Check businesses are active |
| Brevo / Resend | Free email quotas | Outreach and notifications |
| n8n (self-hosted) | Free | Automations |
| Hunter.io | Small free quota | Email finding for top leads only |

### 10.5 Business basics
| Item | Cost | When |
|---|---|---|
| ABN | ✅ Done | — |
| Domain name | About $10–20 a year | After the first payment (or earlier if there's no other way to email professionally) |
| Payments: Stripe or PayPal invoices | Free to set up, fee per transaction | Now |
| GST registration | Only needed above $75k a year turnover | Later |

---

## 11. What has been built so far

| File | Description | Status |
|---|---|---|
| `chat-demo.html` | **Demo 1.** Scripted chat booking assistant for the made-up "Foreshore Fade Co." (barber), with an owner diary. Runs in any browser. Saves to the browser only. | ✅ Works. Visual demo, **not real AI**. |
| `server.js` | **Demo 2 backend.** Node.js, no packages. Tools for checking times and booking, plus a `/bookings` diary page. Built for the **phone add-on** via Vapi, but the same tool design will power the chat product. | ✅ Tested locally |
| `assistant-prompt.md` | AI instructions and first message for Vapi | ⚠️ Untested on a live call. The date syntax needs checking. |
| `tools.json` | Tool definitions for Vapi | ⚠️ Server URL to be filled in |
| `README.md` | Setup steps (Render + Vapi) | ✅ |

**Local test results for `server.js`:** free-slot lookup ✅ · booking ✅ · overlapping booking refused ✅ · closed day handled ✅ · live voice call ❌ not yet done.

**Technical notes:** endpoint `POST /vapi/tools` (Vapi `toolCallList` format) · dates `YYYY-MM-DD`, times 24-hour `HH:MM`, timezone `Australia/Sydney` · optional `TOOL_SECRET` checked against the `x-vapi-secret` header · data in `bookings.json` (not durable on free hosting) · shop config in the `SHOP` object.

**Gap:** the **core product (a real AI website chat) is not built yet**. The existing demos prove the booking flow and the phone add-on.

---

## 12. Plan and roadmap

### Phase 0: Dry test run of what exists (week 1, $0)
| Task | Done when |
|---|---|
| Run `server.js` locally and expose it with a tunnel | A public URL works |
| Set up Vapi with the prompt and tools, and test by browser "Talk" | 5 voice bookings in a row are correct |
| Open `chat-demo.html` and record both demos | 2 short videos exist |

### Phase 1: Build the core product MVP (weeks 1–3, $0)
| Task | Detail |
|---|---|
| Real AI chat backend | Reuse the `server.js` tools. Add a `/chat` endpoint calling a free AI API. |
| Embeddable widget | `<script src=".../widget.js" data-client="hamilton-physio">` |
| Multi-client config | One JSON or database row per client: services, hours, FAQ, branding, notification email |
| Lead capture + owner notification | Email the owner every lead and booking |
| Simple owner page | List of leads, bookings and conversations |
| Deploy | Oracle Cloud Always Free |

### Phase 2: Lead engine (weeks 2–4, $0, run alongside Phase 1)
| Task | Detail |
|---|---|
| Collector script | OSM Overpass (+ Google Places free allowance) for chosen verticals around Newcastle |
| Website profiler | Detect chat, AI, booking and platform signatures (Section 8.3) |
| Scoring + contact finder | Section 8.4–8.5 rules |
| Tracker | Google Sheet (or SQLite) with the fields from 8.6 |
| First 200 leads | Clinics + salons, scored |

### Phase 3: Outreach and first clients (weeks 4–8)
| Task | Detail |
|---|---|
| Domain + sending email | Bought from the first payment, or the cheapest possible option |
| Automated sequences | 3-email sequence, 20–30 a day, auto status updates |
| Personalised demo pages | For leads scoring 60 or more |
| Demos | Video calls, plus visits along transport routes |
| **Target** | **3 paying clients** (founder pricing) |

### Phase 4: Deliver and stabilise (months 2–3)
Onboarding checklist · durable database · backups · monthly reports · testimonials and case studies · first add-on sales (phone, reminders, reviews).

### Phase 5: Scale (months 3–6)
Move to a paid VPS once needed · client self-service settings · integrations (Cliniko, Fresha, Google Calendar) · messaging channels · expand verticals and areas · **target 10–15 clients**.

### Phase 6: Grow the server business (6 months+)
Managed websites, self-hosted tools and email for existing clients · possibly a referral or reseller programme for other agencies (true white label).

### Milestones
| Milestone | Target |
|---|---|
| Demos recorded | End of week 1 |
| AI chat MVP live on free server | Week 3 |
| 200 profiled leads in tracker | Week 4 |
| First paying client | Week 6–8 |
| $1,000 a month recurring | Month 3–4 |
| $2,000+ a month recurring | Month 6 |

---

## 13. Scope

### In scope (now)
- Core AI receptionist: website chat, FAQ, lead capture, booking or booking requests
- Multi-client platform on our own free-tier server
- Phone answering **as an add-on** (Demo 2 exists)
- Automated lead collection, profiling, scoring, tracker and email outreach
- Verticals: allied health clinics, dental, salons and barbers (first), then trades and others
- Area: Newcastle and surrounds

### Out of scope (for now)
- Paid ads
- Buying hardware or reselling raw servers on their own
- Paid white-label platforms (e.g. GoHighLevel) before revenue
- Medical triage or any clinical advice
- Deep integrations before there's a paying client who needs them
- National or overseas expansion

---

## 14. Risks, compliance and open questions

### 14.1 Compliance (Australia)
| Area | What it means for us |
|---|---|
| **Spam Act 2003** | Commercial emails need consent. "Inferred consent" can apply when a business **publishes its email** and our message is **relevant to its work**. Every email must **identify us** and include a **working unsubscribe**, and opt-outs must be honoured within 5 business days. No bought lists, no guessed emails. |
| **Privacy Act / APPs** | Handle client customers' data carefully, keep it minimal, store it securely, and write a privacy policy. **Health info is sensitive**, so take extra care with clinics. |
| **Website and API terms** | Use official APIs (Google Places, Overpass, ABN Lookup). Don't scrape sites whose terms forbid it. Keep request rates polite. |
| **AI disclosure** | Make it clear to callers and chatters that they're talking to an AI assistant. |
| **Do Not Call Register** | Applies to telemarketing calls. Check before cold calling. |

### 14.2 Business risks
| Risk | Mitigation |
|---|---|
| Free tiers change or stop | Keep the code portable (Docker). Be ready to move to a cheap VPS paid from client fees. |
| AI gives wrong info or makes up times | Strict prompts, tool-only availability, human hand-off, testing |
| Businesses already have tools | Profile them, pitch "we add what's missing", integrate |
| Slow sales | Automated outreach volume, personalised demos, founder pricing |
| Support load | Templates, monitoring, clear support hours in the contract |
| AI usage costs grow | Price per package, cap usage, choose efficient models |

### 14.3 Open questions
1. Brand name and domain?
2. Which vertical converts best: clinics or salons? (Test both in Phase 3.)
3. Which AI model for production, balancing cost and quality?
4. Contract terms: minimum term, cancellation, support hours?

---

## 15. Rules and next actions for whoever takes over

### Rules
1. **$0 upfront.** No paid service until a client has paid. Prefer free tiers and open source.
2. **Plain English** with the owner. Explain every step.
3. **The core product is the AI receptionist (chat).** Phone is an add-on. Barbers were only the first demo example.
4. **Build the sales engine alongside the product.** Leads matter as much as code.
5. **Stay legal:** Spam Act, Privacy Act, platform terms (Section 14).
6. Be honest with prospects: don't present scripted demos as real AI.
7. Keep the code simple and portable (Node.js, minimal packages, Docker-ready).

### Immediate next actions
| # | Action | Owner |
|---|---|---|
| 1 | Dry run Demo 2 with a Vapi browser call | Owner |
| 2 | Build the lead collector + website profiler script (Section 8) | Owner / AI |
| 3 | Build the real AI chat MVP + embeddable widget (Phase 1) | Owner / AI |
| 4 | Set up the Google Sheet tracker with the Section 8.6 fields | Owner / AI |
| 5 | Pick a brand name. Buy a domain after the first payment, or when outreach needs it. | Owner |
