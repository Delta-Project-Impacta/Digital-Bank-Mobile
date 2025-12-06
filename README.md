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



