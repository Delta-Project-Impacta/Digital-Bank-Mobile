const {onRequest} = require("firebase-functions/v2/https");
const {onCall} = require("firebase-functions/v2/https");
const {getFirestore} = require("firebase-admin/firestore");
const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.createPixTransaction = functions.https.onCall(async (data, context) => {
  // Ainda não vamos implementar nada aqui.
  return { message: "Função pronta, mas ainda sem lógica." };
});

exports.sendPix = onCall(async (request) => {
  const db = getFirestore();
  const { fromUid, toUid, amount, transPassword } = request.data;
  if (!fromUid || !toUid || !amount || !transPassword) {
    throw new Error("Dados incompletos");
  }
  return await db.runTransaction(async (t) => {
    const fromRef = db.collection("users").doc(fromUid);
    const toRef = db.collection("users").doc(toUid);
    const fromData = (await t.get(fromRef)).data();
    const toData = (await t.get(toRef)).data();

    // 1. Validar senha
    if (fromData.transactionPassword !== transPassword) {
      throw new Error("Senha de transação incorreta");
    }
    // 2. Checar saldo
    if (fromData.Balance < amount) {
      throw new Error("Saldo insuficiente");
    }
    // 3. Debitar e creditar
    t.update(fromRef, { Balance: fromData.Balance - amount });
    t.update(toRef, { Balance: toData.Balance + amount });
    const timestamp = new Date();
    // 4. Transação do sender
    const fromTrans = fromRef.collection("transactions").doc();
    t.set(fromTrans, {
      id: fromTrans.id,
      type: "pix_sent",
      amount,
      to: toUid,
      timestamp,
    });
    // 5. Transação do receiver
    const toTrans = toRef.collection("transactions").doc();
    t.set(toTrans, {
      id: toTrans.id,
      type: "pix_received",
      amount,
      from: fromUid,
      timestamp,
    });
    return { message: "PIX enviado com sucesso" };
  });
});

