<div align="center">
  <h1>Loja Virtual — Sistema Desktop em Java + Swing</h1>
  <p>Aplicação desktop completa de e-commerce desenvolvida em Java puro com interface gráfica Swing, arquitetura em camadas, persistência em arquivos e aplicação prática dos pilares de Programação Orientada a Objetos.</p>
</div>

<br/>

## Sobre o Projeto
Trabalho Prático 2 desenvolvido para a disciplina de **Programação Orientada a Objetos (BCC 138)** na **Universidade Federal de Ouro Preto (UFOP)**, ministrada pelo **Prof. Guillermo Cámara-Chávez**.

O sistema simula o fluxo operacional completo de uma **Loja Virtual**, contando com autenticação, persistência automática em arquivos `.txt`, validação de regras de negócio e interfaces gráficas distintas para **Clientes** e **Administradores**.

---

## Perfis de Usuário e Funcionalidades

### Cliente
* **Autenticação:** Cadastro de novos clientes e login no sistema.
* **Catálogo:** Consulta de produtos físicos e digitais com filtro por categoria.
* **Carrinho e Checkout:** Adição, remoção e alteração de quantidades com validação automática de estoque, cálculo dinâmico de frete e seleção da forma de pagamento (**Pix**, **Cartão de Crédito** ou **Boleto**).
* **Pedidos:** Resumo detalhado na finalização, acompanhamento de status e cancelamento de pedidos.

### Administrador
* **Gestão de Catálogo (CRUD):** Cadastro, consulta, alteração e remoção de produtos físicos (com peso e estoque limitado) e digitais (com tamanho do arquivo, link para download e estoque ilimitado).
* **Gestão de Pedidos:** Atualização da situação dos pedidos e confirmação manual de pagamentos via boleto.
* **Relatórios Gerenciais:** Consulta de faturamento total, pedidos agrupados por situação, produtos mais vendidos e alerta de estoque baixo.

---

## Regras de Negócio Implementadas

| Módulo | Regra / Comportamento |
| :--- | :--- |
| **Pix** | Concede **5% de desconto** sobre o total do pedido; o pedido já nasce com status `PAGO`. |
| **Cartão de Crédito** | Parcelamento de **1x a 6x** (sem juros até 3 parcelas; a partir da 4ª parcela, aplica taxa de juros de **2% ao mês**); nasce como `PAGO`. |
| **Boleto Bancário** | Sem descontos ou acréscimos; o pedido permanece como `AGUARDANDO_PAGAMENTO` até que sua confirmação seja registrada pelo administrador. |
| **Cálculo de Frete** | **Grátis** para compras com subtotal igual ou superior a `R$ 300,00` ou carrinhos contendo apenas produtos digitais. Demais casos: `R$ 5,00` fixo + `R$ 2,00` por quilo. |
| **Controle de Estoque** | Validação em tempo real para produtos físicos, com baixa automática na finalização da compra e devolução ao estoque em caso de cancelamento. Produtos digitais possuem estoque ilimitado. |
| **Produtos** | Geração automática de código identificador; o tipo (`Físico` ou `Digital`) é definido no cadastro. |

---

## Arquitetura e Conceitos de POO Aplicados

O projeto segue uma divisão modular em pacotes (`modelo`, `repositorio`, `servico`, `visao`, `excecao`) sem uso de bibliotecas externas:

* **Classes Abstratas e Herança:** `Produto`, `Usuario`, `FormaPagamento`, `LojaException`, `Repositorio` e `TelaBase`.
* **Polimorfismo:** Sobrescrita de comportamentos específicos como `temEstoque()`, `calcularTotal()` e `paraLinha()`.
* **Encapsulamento:** Atributos privados (`private`) protegidos por *getters* e *setters* com validação de integridade.
* **Coleções Genéricas:** Estruturas dinâmicas em memória utilizando `ArrayList` e `HashMap`.
* **Tratamento de Exceções:** Hierarquia própria de exceções de negócio herdando de `LojaException`, capturadas e exibidas amigavelmente na interface Swing.
* **Downcasting Seguro:** Aplicado na manipulação de componentes e modelos específicos (`DialogoProduto`, `ProdutoTableModel`, `PainelCatalogo`, `TelaLogin`, `TelaCheckout`).

### Estrutura de Diretórios
```text
projeto/
├── src/
│   └── loja/
│       ├── app/          # Main (ponto de entrada da aplicação)
│       ├── modelo/       # Entidades (Produto, Usuario, Pedido, Pagamento)
│       ├── excecao/      # Exceções de negócio (LojaException e subclasses)
│       ├── repositorio/  # Coleções em memória e persistência em .txt
│       ├── servico/      # Regras de negócio e validações
│       └── visao/        # Interfaces gráficas Swing (comum, cliente, admin)
├── dados/                # Arquivos de persistência (produtos.txt, usuarios.txt, pedidos.txt)
└── README.md
```

---

## Como Compilar e Executar

### Pré-requisitos
* **JDK [PREENCHER: ex. 17]** ou superior instalado e configurado no `PATH` *(testado com JDK [PREENCHER])*.
* Ambiente desktop com suporte a interface gráfica.

### 1. Compilação via Terminal
Abra o terminal na **pasta raiz do projeto** (onde estão `src` e `dados`) e execute o comando correspondente ao seu sistema:

**Linux / macOS:**
```bash
mkdir -p out
find src -name "*.java" > fontes.txt
javac -encoding UTF-8 -d out @fontes.txt
```

**Windows (Prompt de Comando / CMD):**
```cmd
mkdir out
dir /s /b src\*.java > fontes.txt
javac -encoding UTF-8 -d out @fontes.txt
```

**Windows (PowerShell):**
```powershell
mkdir out
Get-ChildItem -Recurse src -Filter *.java | ForEach-Object { $_.FullName } | Out-File -Encoding ascii fontes.txt
javac -encoding UTF-8 -d out "@fontes.txt"
```

### 2. Execução
Ainda na pasta raiz do projeto, inicie a aplicação:
```bash
java -cp out loja.app.Main
```

> **Dados Iniciais e Persistência:** Ao ser iniciado pela primeira vez, caso a pasta `dados/` não exista, o sistema carrega automaticamente um conjunto pré-cadastrado de produtos, clientes, administrador e pedidos finalizados em arquivos `.txt` para permitir testes imediatos. Para restaurar os dados originais, encerre o programa, apague os arquivos dentro de `dados/` e execute novamente.

---

## Solução de Problemas Comuns

* **`javac` não é reconhecido:** Verifique se você instalou o **JDK** (e não apenas o JRE) e reinicie o terminal após configurar a variável `PATH`.
* **`Could not find or load main class loja.app.Main`:** Certifique-se de estar executando o comando exatamente na pasta raiz do projeto e que a pasta `out/` foi gerada.
* **Caracteres com acento corrompidos:** Garanta que a flag `-encoding UTF-8` foi utilizada na compilação.
* **`HeadlessException`:** O ambiente atual não possui suporte a janelas gráficas (ex: terminal remoto sem interface).

---

## Equipe e Divisão de Tarefas

* **Stephany Fernandes Torres - 25.1.4022** — [GitHub](https://github.com/stephanyft)
* **Maria Eduarda de Carvalho Silva - 25.1.4035** — [GitHub](https://github.com/mariaecs545)
* **[PREENCHER Nome]** — *[PREENCHER módulo]*
* **[PREENCHER Nome]** — *[PREENCHER módulo]*
