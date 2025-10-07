<div align="center">

# 🧾 **RazonetesApp**
### Razonete • Balancete • ARE • Balanço Patrimonial

*Registro e Relatórios Contábeis em Java Swing*

---

[📖 Sobre](#-sobre-o-projeto) • 
[⚙️ Funcionalidades](#️-funcionalidades) • 
[💻 Tecnologias](#-tecnologias-utilizadas) • 
[🚀 Como Executar](#️-como-executar-o-projeto-localmente) • 
[🧪 Testes](#-testes) • 
[🤝 Contribuição](#-contribuição) • 
[📞 Contato](#-contato)

---

📱 **Demonstração**
<p align="center">
  <img src="https://github.com/rogeramorim7/RazonetesApp/blob/main/Simulador-de-Razonete-Cont%C3%A1bil-2025-10-07-13-45-24.gif" alt="Demonstração do RazonetesApp" width="700">
</p>

<h4 align="center">Aplicação completa e funcional — pronta para estudos e prática contábil.</h4>

---

</div>

## 📖 Sobre o Projeto

O **RazonetesApp** é uma aplicação desktop desenvolvida em **Java 25 (Swing)** com foco em **ensino e prática contábil**.  
Permite registrar lançamentos, classificar contas automaticamente e gerar relatórios contábeis essenciais — tudo em uma interface gráfica intuitiva.

### 🎯 Objetivos Principais
- Visualizar **T-Contas (Razonetes)** com movimentação dinâmica  
- Gerar **Balancete**, **Balanço Patrimonial**, **DRE** e **ARE**  
- Aplicar **boas práticas de POO, modularização e camadas**  
- Integrar **Contabilidade + Tecnologia** de forma didática

---

## ⚙️ Funcionalidades

✅ **Cadastro de Lançamentos**
- Tipo: Débito ou Crédito  
- Campos: data, valor e descrição  
- Armazenamento dinâmico em memória  

🧠 **Classificação Automática de Contas**
- Lógica de natureza contábil (Ativo, Passivo, PL, Receita, Despesa)

📊 **Relatórios Contábeis**
- **Razonete (T-Conta interativo)**  
- **Balancete** com verificação de saldos  
- **Balanço Patrimonial** (Ativo, Passivo e PL)  
- **Demonstrações de Resultado (DRE e ARE)**  

🖥️ **Interface Gráfica**
- Navegação separada por relatório  
- Interface intuitiva e moderna  
- Exportação dos relatórios em **CSV**

---

## 💻 Tecnologias Utilizadas

| Tecnologia | Badge | Descrição |
|-------------|--------|-----------|
| **Java 25** | ![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge&logo=oracle&logoColor=white) | Linguagem principal |
| **Swing** | ![Swing](https://img.shields.io/badge/Swing-000000?style=for-the-badge&logo=java&logoColor=white) | Biblioteca GUI desktop |
| **Git & GitHub** | ![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white) | Controle de versão e repositório |

---

## 🚀️ Como Executar o Projeto Localmente

### 📋 Pré-requisitos
- ☕ **JDK 25+** instalado  
- 🧩 IDE de sua preferência (IntelliJ IDEA, Eclipse ou VS Code)

---

### 🔧 Passo a Passo

```bash
# Clone o repositório
git clone https://github.com/rogeramorim7/RazonetesApp.git
cd RazonetesApp

# Compile as classes
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

# Execute a aplicação
java -cp bin com.minhaempresa.razao.app.MainApplication
```

⚙️ Configurações Opcionais

Ajuste parâmetros no arquivo settings.json:
```bash
{
  "dataFormat": "dd/MM/yyyy",
  "exportPath": "./exports",
  "locale": "pt-BR"
}
```

📸 Screenshots
<div align="center"> <img src=".github/screenshots/razonete.png" alt="Razonete GUI" width="45%"/> <img src=".github/screenshots/balancete.png" alt="Balancete GUI" width="45%"/> </div>

```bash
🧪 Testes

Execute os testes automatizados com JUnit:
```

🤝 Contribuição

Contribuições são sempre bem-vindas 💙
Siga o fluxo padrão:

```bash
# 1️⃣ Faça um Fork
# 2️⃣ Crie uma Branch
git checkout -b feature/nome-da-feature

# 3️⃣ Faça commits
git commit -m "Adiciona nova funcionalidade"

# 4️⃣ Envie para o repositório remoto
git push origin feature/nome-da-feature

# 5️⃣ Abra um Pull Request 🚀
```

📞 Contato
<div align="center"> <a href="https://www.linkedin.com/in/roger-de-amorim-300a14307/" target="_blank"> <img src="https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn"/> </a> <a href="https://github.com/rogeramorim7" target="_blank"> <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/> </a> </div>

<sub align="center"> Feito com ☕ e dedicação por **rogeramorim7** — unindo **Contabilidade, Lógica e Java Swing** em um projeto completo de aprendizado. </sub>
