 Digital Bank Mobile Delta

**App bancário digital em Kotlin + xml (em desenvolvimento)**  

## ChatBot Kontein — Assistente Virtual com Gemini AI

O **Kontein** é o assistente virtual oficial do Delta Bank, integrado diretamente no app mobile via **Google Gemini API** (`gemini-2.5-flash`).

### Funcionalidades do Kontein
- Conversa natural com o usuário
- Fluxo guiado 100% seguro de **Recuperação de Senha**
- Respostas rápidas, amigáveis e dentro das políticas bancárias
- **Zero acesso** a dados sensíveis ou operações financeiras

### Regras rígidas do System Prompt (segurança bancária)
| Regra                     | Aplicada? |
|---------------------------|-----------|
| Nunca realiza transações    | Yes       |
| Nunca solicita dados sensíveis | Yes       |
| Só guia recuperação de senha | Yes       |
| Tom profissional e amigável  | Yes       |
| Bloqueia perguntas externas | Yes       |

---

## Configuração da API Gemini (OBRIGATÓRIO)

### Passo 1: Obter a chave
1. Acesse: https://aistudio.google.com/app/apikey
2. Clique em **"Create API key"**
3. Copie a chave gerada

> **NUNCA** commite a chave no GitHub!

### Passo 2: Adicionar ao projeto

Edite o arquivo `local.properties` (na raiz do projeto):

```properties
GEMINI_API_KEY=SUA_CHAVE_AQUI
```

> O **local.properties** já está no `.gitignore`, portanto sua chave nunca será enviada para o repositório. A injeção da chave ocorre automaticamente via `BuildConfig`.

🚀 Setup do Projeto Digital Bank Mobile
Guia completo para configurar o ambiente local, Firebase Functions, Node e App Check.

1️⃣ Pré-requisitos

Node.js v22.21.1 (via NVM)
Git
Android Studio
Firebase CLI (firebase-tools)

Ter outra versão do Node pode causar erros no build do Functions ou incompatibilidade de dependências.

2️⃣ Configurando Node via NVM
a) Verificar Node > node -v
Deve mostrar v22.21.1

Se não, prossiga com NVM.

b) Instalar/usar Node correto

Se não tiver NVM:

Windows: NVM for Windows > https://github.com/coreybutler/nvm-windows/releases
Desça até "Assets" e selecione o arquivo "nvm-setup.exe"

c) Depois de baixar, instale e rodo no PowerShell (ou no Terminal do projeto):
nvm install 22.21.1
nvm use 22.21.1
node -v  <  # confirma a versão

d) Se tiver Node diferente, desinstale a antiga e instale a versão correta via NVM. Se for necessário desinstalar, certifique-se que a pasta no diretório também foi excluída, senão, exclua. 

e) Caso tenha algum erro, certifique-se que as variáveis de ambiente no Path (das Variáveis do sistema e não de usuários) estão configurados o caminho pro seu nvm e nodejs. 

Ex (nessa ordem, nvm primeiro, nodejs depois) > C:\Program Files\nvm
     						C:\Program Files\nodejs


3️⃣ Atualizando o projeto local
Puxe as alterações da main pro seu projeto local (comando: git pull origin main)

4️⃣ Instalando dependências do Firebase Functions
1. cd functions
2. npm install

5️⃣ Configurando Firebase CLI
1. Login> firebase login
2. Associar projeto > firebase use --add

6️⃣ Deploy das Functions
1. Aumentar timeout do deploy (projeto grande): set FUNCTIONS_DISCOVERY_TIMEOUT=30
2. Deploy: firebase deploy --only functions

Aguarde a finalização do deploy e verifique se foi completado com sucesso. Se sim, rode a aplicação e teste o fluxo de transferência. 


