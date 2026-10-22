# ManagerDroid Helper

Um aplicativo complementar ao **ManagerDroid**, projetado para extrair os nomes e ícones dos aplicativos instalados em um dispositivo Android.

O ManagerDroid usa o Helper para mostrar os apps com nome e ícone reais, de forma rápida. Sem ele, o ManagerDroid ainda funciona, mas mostra os apps pelo nome do pacote e sem ícones.

---

## Como compilar (fácil)

1. **Baixe o projeto em `.zip`** (o arquivo `ManagerDroid-Helper-src.zip`).
2. Acesse **https://newly.app/pt/apk-builder** e crie uma conta.
3. **Envie o projeto** (o arquivo `.zip`) e coloque o **nome do pacote** como:

   ```text
   com.managerdroid.help
   ```

4. Depois de enviar, aperte em **Build**.
5. Ao final, baixe o APK gerado.

---

## Depois de compilar

- Instale o APK no celular. Se já houver um helper antigo instalado com outro nome de pacote (`com.droidcontrol.helper`), desinstale-o antes.
- Abra o **ManagerDroid Helper** uma vez e toque em **Permitir acesso a arquivos**, para autorizar a leitura do armazenamento.
- Pronto: o ManagerDroid no computador passa a usar o helper automaticamente.

---

## Observações

- O nome do pacote precisa ser exatamente `com.managerdroid.help`. Se for diferente, o ManagerDroid não reconhece o helper.
- O Helper não tem interface de uso no dia a dia. Ele só exporta os dados de que o computador precisa, na pasta `/sdcard/ManagerDroid`.
