# **RazonetesApp**

**Aplicação Desktop em Java para Simulação e Estudo Contábil**

[![Java](https://img.shields.io/badge/Java-21+-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen?style=for-the-badge)](#[Status-do-Build])
[![Licença](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)

O **RazonetesApp** é uma poderosa ferramenta **desktop** desenvolvida em **Java** (Swing) para **estudantes e profissionais de contabilidade**. Ela facilita o aprendizado e a prática da escrituração contábil, permitindo o registro de lançamentos (débito/crédito) e a geração imediata de relatórios financeiros essenciais.

---

## 📋 Índice

- [ Funcionalidades Principais](#-funcionalidades-principais)
- [ Arquitetura do Projeto](#-arquitetura-do-projeto)
- [ Instalação e Execução](#-instalação-e-execução)
- [Pré-requisitos](#pré-requisitos)
- [Compilação e Execução](#compilação-e-execução)
- [ Configuração](#-configuração)
- [ Exemplos de Uso](#-exemplos-de-uso)
- [ Screenshots](#-screenshots)
- [ Testes Unitários](#-testes-unitários)
- [ Contribuição](#-contribuição)
- [ Licença](#-licença)

---

## Funcionalidades Principais

O aplicativo oferece um ambiente completo para a simulação contábil:

* **Lançamentos Contábeis:** Registro intuitivo de transações com designação clara de **Débito (D)** e **Crédito (C)**.
* **Classificação Automática:** Sistema robusto para classificação e agrupamento automático das contas contábeis.
* **Geração de Relatórios Interativos:**
    * **Razonete (T-Contas):** Visualização interativa do movimento e saldo das contas.
    * **Balancete de Verificação:** Relatório para conferência de saldos (Débitos vs. Créditos).
    * **Balanço Patrimonial:** Demonstração do Ativo, Passivo e Patrimônio Líquido (PL).
    * **DRE (Demonstração do Resultado do Exercício):** Cálculo do resultado (Lucro ou Prejuízo).
    * **ARE (Demonstração do Resultado Abrangente):** Demonstração do resultado total.
* **Interface Gráfica (GUI):** Implementada com **Java Swing**, com módulos de interface isolados para cada relatório.
* **Configuração Flexível:** Customização de parâmetros da aplicação (formato de data, *locale*, caminhos de exportação) via arquivo `settings.json`.
* **Exportação:** Opção de exportar relatórios gerados em formatos como **CSV** ou **PDF**.

---

RazonetesApp/
├── bin/                        # Classes compiladas (.class)
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/minhaempresa/razao/
│   │           ├── app/        # Ponto de entrada (MainApplication)
│   │           ├── core/       # Lógica de negócio, modelos e regras contábeis
│   │           └── gui/        # Módulos da Interface Gráfica (Java Swing)
│   └── test/                   # Código dos Testes Unitários (JUnit)
└── settings.json               # Arquivo de configurações da aplicação

---

## Instalação e Execução

### Pré-requisitos

* **Java Development Kit (JDK) 17+** (Recomendado JDK 21).
* **Git**.

### Compilação e Execução

1.  **Clone o repositório:**
    ```bash
    git clone [https://github.com/SEU_USUARIO/RazonetesApp.git](https://github.com/SEU_USUARIO/RazonetesApp.git)
    cd RazonetesApp
    ```

2.  **Compile o projeto:**
    Se você usa ferramentas de *build* (Maven/Gradle):
    ```bash
    # Com Maven
    mvn clean install
    
    # Ou com Gradle
    gradle build
    ```
    Ou, compile diretamente usando `javac`:
    ```bash
    find src -name "*.java" > sources.txt
    javac -d bin @sources.txt
    ```

3.  **Execute a aplicação:**
    Após a compilação, inicie a aplicação a partir do diretório raiz:
    ```bash
    java -cp bin com.minhaempresa.razao.app.MainApplication
    ```

---

## Configuração

O comportamento da aplicação pode ser ajustado modificando o arquivo de configurações **`settings.json`** na raiz do projeto.

**Exemplo de `settings.json`:**

```json
{
  "dataFormat": "dd/MM/yyyy", 
  "exportPath": "./exports", 
  "locale": "pt-BR",
  "defaultCurrency": "BRL" 
}
Exemplos de Uso
A navegação é feita via menu lateral:

Registro de Lançamentos: Insira as transações, especificando a conta, o valor e o tipo (Débito/Crédito).

Visualização de Razonete: Selecione uma conta para visualizar seu T-Conta e saldo.

Geração de Balancete: Gere o Balancete para conferência de saldos.

Exportação: Utilize o botão "Exportar" disponível nos módulos de relatórios para salvar as demonstrações (CSV/PDF).

Screenshots
Substitua os placeholders abaixo pelas imagens reais da sua aplicação.

<div align="center">
<h3>Razonete (T-Contas)</h3>
<img src=".github/screenshots/razonete.png" alt="Razonete GUI" width="40%" />




<h3>Balancete de Verificação</h3>
<img src=".github/screenshots/balancete.png" alt="Balancete GUI" width="40%" />
</div>

Testes Unitários
O projeto utiliza JUnit para garantir a integridade da lógica de negócio (Core).

Bash

# Com Maven
mvn test

# Com Gradle
gradle test
Contribuição
Faça um fork deste repositório.

Crie uma branch: git checkout -b feature/nome-da-feature.

Commite suas alterações: git commit -m "feat: Adiciona nova funcionalidade X".

Envie suas mudanças: git push origin feature/nome-da-feature.

Abra um Pull Request (PR) detalhado.

Licença
Este projeto é distribuído sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

## Arquitetura do Projeto

O projeto adota uma estrutura modular para separar a lógica de negócio (*Core*) da interface gráfica (*GUI*) e da inicialização (`app`).
