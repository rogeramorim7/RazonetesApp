<div align="center">
💼 RazonetesApp
🇧🇷 Registro e Relatórios Contábeis em Java Swing
<p> <a href="#-sobre-o-projeto">Sobre</a> - <a href="#-funcionalidades">Funcionalidades</a> - <a href="#-tecnologias">Tecnologias</a> - <a href="#️-como-executar">Como Executar</a> - <a href="#-contato">Contato</a> </p> </div>
📱 Demonstração da Aplicação
<p align="center"> <img src=".github/screenshots/razonete.gif" alt="GIF do RazonetesApp" width="700"> </p> <h4 align="center"> Aplicação Completa ✅ </h4>
🚀 Sobre o Projeto
RazonetesApp é uma ferramenta desktop desenvolvida em Java usando Swing para estudos e práticas contábeis. Permite o registro de lançamentos, classificação automática de contas e geração dinâmica de relatórios contábeis essenciais.

Objetivos Principais
Exibir T-Contas (Razonetes) para análise de débitos e créditos

Gerar Balancete, Balanço Patrimonial, DRE e ARE

Demonstrar boas práticas de modularização, POO e interface gráfica

⚙️ Funcionalidades
 Cadastro de Lançamentos

Tipo Débito ou Crédito

Data, valor e descrição

 Classificação de Contas

Lógica para natureza de cada conta

 Relatórios Contábeis

Razonete (T-Conta interativo)

Balancete (conferência de saldos)

Balanço Patrimonial (Ativo, Passivo, PL)

Demonstração do Resultado do Exercício (DRE)

Demonstração do Resultado Abrangente (ARE)

 Interface Gráfica

Separada por relatório

Navegação intuitiva

Exportação de relatórios via CSV

🏗️ Tecnologias Utilizadas
<table> <tr> <td><strong>Tecnologia</strong></td> <td><strong>Badge</strong></td> <td><strong>Descrição</strong></td> </tr> <tr> <td>Java 17</td> <td><img src="https://img.shields.io/badge/Java-17-blue?style=for-the-badge&logo=java&logoColor=white"></td> <td>Linguagem principal</td> </tr> <tr> <td>Swing</td> <td><img src="https://img.shields.io/badge/Swing-000000?style=for-the-badge&logo=java&logoColor=white"></td> <td>Biblioteca para GUI desktop</td> </tr> <tr> <td>Git & GitHub</td> <td><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white"></td> <td>Controle de versão</td> </tr> </table>
🛠️ Como Executar o Projeto Localmente
📋 Pré-requisitos
Java Development Kit (JDK) 17+

IDE compatível (IntelliJ IDEA, Eclipse, VS Code)

🔧 Passo a Passo
Clone o repositório

bash
git clone https://github.com/SEU_USUARIO/RazonetesApp.git
cd RazonetesApp
Compile as classes

bash
find src -name "*.java" > sources.txt
javac -d bin @sources.txt
Execute a aplicação

bash
java -cp bin com.minhaempresa.razao.app.MainApplication
⚙️ Configurações
Edite o arquivo settings.json para ajustar parâmetros:

json
{
  "dataFormat": "dd/MM/yyyy",
  "exportPath": "./exports",
  "locale": "pt-BR"
}
📸 Screenshots
<div align="center"> <img src=".github/screenshots/razonete.png" alt="Razonete GUI" width="45%" /> <img src=".github/screenshots/balancete.png" alt="Balancete GUI" width="45%" /> </div>
Adicione suas telas reais em .github/screenshots/.

🧪 Testes
Execute testes JUnit:

bash
mvn test
# ou
gradle test
🤝 Contribuição
Faça um Fork

Crie uma Branch (git checkout -b feature/nome-da-feature)

Commit suas alterações (git commit -m "Descrição")

Push para o repositório remoto

Abra um Pull Request

📞 Contato
<div align="center"> <a href="https://www.linkedin.com/in/SEU_USUARIO/" target="_blank"> <img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" alt="LinkedIn"/> </a> <a href="https://github.com/SEU_USUARIO" target="_blank"> <img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/> </a> </div>
<sub>Feito por SEU_NOME — Aprendizado de lógica contábil e Java Swing.</sub>
