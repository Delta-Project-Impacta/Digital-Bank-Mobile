const admin = require("firebase-admin");
const crypto = require("crypto");
const { onCall, HttpsError } = require("firebase-functions/v2/https");

admin.initializeApp();

function sha256hex(value) {
  return crypto
    .createHash("sha256")
    .update(value, "utf8")
    .digest("hex");
}

exports.sendPix = onCall(
  {
    region: "us-central1",
    invoker: "public",
    enforceAppCheck: true,
  },
  async (request) => {
    console.log("sendPix chamado");
    console.log("raw data:", request.data);
    console.log("auth presente?", !!request.auth);
    console.log("app check presente?", !!request.app);

    try {
      if (!request.auth || !request.auth.uid) {
        throw new HttpsError("unauthenticated", "Usuário não autenticado");
      }

      const fromUid = request.auth.uid;
      const data = request.data || {};
      const toUid = data.toUid;
      const amount = data.amount;
      const transactionPassword = data.transactionPassword;

      if (!toUid || typeof toUid !== "string") {
        throw new HttpsError("invalid-argument", "toUid inválido");
      }

      if (amount === undefined || isNaN(Number(amount))) {
        throw new HttpsError("invalid-argument", "amount inválido");
      }

      if (!transactionPassword || typeof transactionPassword !== "string") {
        throw new HttpsError("invalid-argument", "transactionPassword inválida");
      }

      const value = Number(amount);
      if (value <= 0) {
        throw new HttpsError("invalid-argument", "amount deve ser > 0");
      }

      const db = admin.firestore();

      return await db.runTransaction(async (t) => {
        const fromRef = db.collection("users").doc(fromUid);
        const toRef = db.collection("users").doc(toUid);

        const [fromSnap, toSnap] = await Promise.all([
          t.get(fromRef),
          t.get(toRef),
        ]);

        if (!fromSnap.exists || !toSnap.exists) {
          throw new HttpsError("not-found", "Usuário não encontrado");
        }

        const fromData = fromSnap.data() || {};
        const toData = toSnap.data() || {};

        if (!fromData.transactionPassword) {
          throw new HttpsError("failed-precondition", "Senha não configurada");
        }

        const receivedHash = sha256hex(transactionPassword);
        if (receivedHash !== fromData.transactionPassword) {
          throw new HttpsError("permission-denied", "Senha incorreta");
        }

        const fromBalance = Number(fromData.balance || 0);
        const toBalance = Number(toData.balance || 0);

        if (fromBalance < value) {
          throw new HttpsError("failed-precondition", "Saldo insuficiente");
        }

        const newFromBalance = fromBalance - value;
        const newToBalance = toBalance + value;

        const serverTs = admin.firestore.FieldValue.serverTimestamp();

        t.update(fromRef, { balance: newFromBalance });
        t.update(toRef, { balance: newToBalance });

        const fromTransRef = fromRef.collection("transactions").doc();
        const toTransRef = toRef.collection("transactions").doc();

        const fromName = fromData.name || null;
        const fromCpf = fromData.cpf || null;
        const toName = toData.name || null;
        const toCpf = toData.cpf || null;

        t.set(fromTransRef, {
          id: fromTransRef.id,
          type: "pix_sent",
          amount: -value,
          description: `PIX para ${toName}`,
          timestamp: serverTs,
          to: toUid,
        });

        t.set(toTransRef, {
          id: toTransRef.id,
          type: "pix_received",
          amount: value,
          description: `PIX recebido de ${fromName}`,
          timestamp: serverTs,
          from: fromUid,
        });

        return {
          message: "PIX enviado com sucesso",
          fromTransactionId: fromTransRef.id,
          toTransactionId: toTransRef.id,
          newFromBalance,
          newToBalance,

          origin: {
            uid: fromUid,
            name: fromName,
            cpf: fromCpf,
            institution: "DeltaBank"
          },
          destiny: {
            uid: toUid,
            name: toName,
            cpf: toCpf,
            institution: "DeltaBank"
          },
          amount: value,
          timestamp: new Date().toISOString()
        };
      });
    } catch (err) {
      if (err instanceof HttpsError) throw err;
      throw new HttpsError("internal", err.message || "Erro interno");
    }
  }
);
