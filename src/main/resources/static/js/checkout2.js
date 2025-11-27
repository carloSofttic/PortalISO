// /static/js/checkout.js
(() => {
    // ---------- Metas ----------
    const getMeta = (n) => document.querySelector(`meta[name="${n}"]`)?.content || '';
    const PUB_KEY = getMeta('stripe-public-key');
    const PLAN = getMeta('plan-key') || 'MONTHLY';
    const CSRF = {
      token: getMeta('_csrf'),
      header: getMeta('_csrf_header') || 'X-CSRF-TOKEN'
    };
  
    if (!window.Stripe || !PUB_KEY) {
      console.error('Falta Stripe.js o la meta stripe-public-key');
      return;
    }
  
     // Colores que ya usabas en tu estilo "base" anterior:
     const COLOR_TEXT = "#e2e8f0";
     const COLOR_PLACEHOLDER = "#94a3b8";
     const COLOR_ICON = "#a5b4fc";
     const COLOR_DANGER = "#fecaca";
    // ---------- Stripe / Elements ----------
    const stripe = Stripe(PUB_KEY);
    const elements = stripe.elements();
  
    // ==== Apariencia del Card Element (texto claro) ====
    const cardStyle = {
        base: {
          color: COLOR_TEXT,                 // texto dentro del input
          fontFamily: 'system-ui, -apple-system, Segoe UI, Roboto, Ubuntu, "Helvetica Neue", Arial',
          fontSmoothing: 'antialiased',
          fontSize: '16px',
          '::placeholder': { color: COLOR_PLACEHOLDER },
          iconColor: COLOR_ICON
        },
        invalid: {
          color: COLOR_DANGER,
          iconColor: COLOR_DANGER
        }
      };
  
    const card = elements.create('card', { hidePostalCode: true, style: cardStyle });
    card.mount('#card-element');
    card.on('change', (ev) => {
      const el = document.getElementById('card-errors');
      if (!el) return;
      el.textContent = ev.error ? ev.error.message : '';
      el.classList.toggle('show', !!ev.error);
    });
  
    // ---------- Helpers ----------
    const $ = (id) => document.getElementById(id);
  
    async function createIntent() {
      const headers = {
        'Content-Type': 'application/x-www-form-urlencoded'
      };
      // agrega CSRF si existe
      if (CSRF.token) headers[CSRF.header] = CSRF.token;
  
      const res = await fetch('/checkout/intent', {
        method: 'POST',
        headers,
        body: new URLSearchParams({ plan: PLAN })
      });
  
      if (res.redirected) {
        // te mandó a /login → sesión/CSRF
        throw new Error('No autenticado o CSRF inválido (redirect a /login).');
      }
      if (!res.ok) {
        const txt = await res.text();
        throw new Error(`Intent fallo ${res.status}: ${txt.substring(0, 200)}…`);
      }
      return res.json(); // { clientSecret }
    }
  
    async function pay() {
      const btn = $('payBtn');
      const err = $('card-errors');
      if (!btn) return;
  
      // Asegura type="button"
      if (btn.tagName === 'BUTTON' && btn.type.toLowerCase() !== 'button') {
        btn.type = 'button';
      }
  
      btn.disabled = true;
      btn.classList.add('loading');
      err && (err.textContent = '');
  
      try {
        const { clientSecret } = await createIntent();
  
        const billing_details = {
          name: $('cardholder-name')?.value || undefined,
          email: $('cardholder-email')?.value || undefined,
          address: {
            line1: $('billing-line1')?.value || $('addr-line1')?.value || undefined,
            line2: $('billing-line2')?.value || $('addr-line2')?.value || undefined,
            city: $('billing-city')?.value || $('addr-city')?.value || undefined,
            state: $('billing-state')?.value || $('addr-state')?.value || undefined,
            postal_code: $('billing-postal')?.value || $('addr-zip')?.value || undefined,
            country: $('billing-country')?.value || $('addr-country')?.value || undefined
          }
        };
  
        const { error, paymentIntent } = await stripe.confirmCardPayment(
          clientSecret,
          {
            payment_method: { card, billing_details }
          }
        );
  
        if (error) {
          throw new Error(error.message || 'Error al confirmar el pago.');
        }
  
        // Éxito
        window.location.href = '/dashboard';
      } catch (e) {
        console.error(e);
        if (err) {
          err.textContent = e.message || 'No se pudo procesar el pago.';
          err.classList.add('show');
        }
      } finally {
        btn.disabled = false;
        btn.classList.remove('loading');
      }
    }
  
    $('payBtn')?.addEventListener('click', pay);
  })();
  