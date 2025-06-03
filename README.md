# Sistema de Consulta de Temperatura

Este é um sistema distribuído para consulta de temperaturas de cidades brasileiras, utilizando dados do CPTEC/INPE. O sistema é composto por duas aplicações Spring Boot que se comunicam através de filas AWS SQS.

## Arquitetura do Sistema

O sistema é composto por dois microserviços principais:

### 1. Webapp (Porta 8080)
- Responsável pela interface com o usuário
- Gerencia as solicitações de temperatura
- Processa e armazena os resultados das consultas
- Envia solicitações para processamento através do AWS SQS
- Recebe e processa os resultados das consultas de temperatura

### 2. Scheduler (Porta 8081)
- Responsável pelo processamento das solicitações de temperatura
- Consome mensagens da fila de solicitações
- Realiza consultas ao serviço CPTEC/INPE
- Processa e envia os resultados através do AWS SQS
- Gerencia o agendamento e execução das consultas

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.2.3
- Spring Cloud OpenFeign
- MySQL 8.0
- AWS SQS (LocalStack)
- Docker e Docker Compose
- Maven

## Estrutura do Projeto

O projeto está organizado em dois módulos principais:

📁 **webapp/**
   - Contém a aplicação web principal
   - Principais arquivos:
     - `src/` - Código fonte da aplicação
     - `pom.xml` - Configuração Maven e dependências
     - `Dockerfile` - Instruções para build do container

📁 **scheduler/**
   - Contém o serviço de processamento
   - Principais arquivos:
     - `src/` - Código fonte do scheduler
     - `pom.xml` - Configuração Maven e dependências
     - `Dockerfile` - Instruções para build do container

Na raiz do projeto você encontra:
- `docker-compose.yml` - Orquestração de todos os serviços
- `check-queue.sh` - Script auxiliar para verificação das filas
- Arquivos de documentação e diagramas

## Componentes do Sistema

### Bancos de Dados
- **MySQL Solicitações** (Porta 33007)
  - Armazena as solicitações de temperatura e seus resultados
  - Usado pela aplicação Webapp

- **MySQL Agendamentos** (Porta 33006)
  - Armazena os agendamentos de consultas
  - Usado pela aplicação Scheduler

### Filas SQS (LocalStack - Porta 4566)
- **solicita-temperatura-queue**
  - Fila para solicitações de temperatura
  - Consumida pelo Scheduler

- **retorno-temperatura-queue**
  - Fila para resultados das consultas
  - Consumida pelo Webapp

## Como Executar

### Pré-requisitos
- Docker
- Docker Compose
- Git

### Passos para Execução

1. Clone o repositório:
```bash
git clone [URL_DO_REPOSITORIO]
cd [NOME_DO_DIRETORIO]
```

2. Inicie os serviços com Docker Compose:
```bash
docker-compose up -d
```

3. Verifique se todos os serviços estão rodando:
```bash
docker-compose ps
```

Os serviços estarão disponíveis nos seguintes endereços:
- Webapp: http://localhost:8080
- Scheduler: http://localhost:8081
- LocalStack (SQS): http://localhost:4566
- MySQL Solicitações: localhost:33007
- MySQL Agendamentos: localhost:33006

## Fluxo de Funcionamento

1. O usuário faz uma solicitação de temperatura através do Webapp
2. O Webapp envia a solicitação para a fila `solicita-temperatura-queue`
3. O Scheduler consome a mensagem da fila
4. O Scheduler consulta o serviço CPTEC/INPE
5. O Scheduler processa o resultado e envia para a fila `retorno-temperatura-queue`
6. O Webapp consome o resultado e armazena no banco de dados
7. O usuário pode consultar o resultado através do Webapp

## Monitoramento e Logs

Para visualizar os logs de cada serviço:

```bash
# Logs do Webapp
docker-compose logs -f webapp

# Logs do Scheduler
docker-compose logs -f scheduler

# Logs do LocalStack
docker-compose logs -f localstack
```

## Desenvolvimento

### Estrutura do Código

Ambas as aplicações seguem uma arquitetura limpa com as seguintes camadas:

- **domain**: Entidades e regras de negócio
- **application**: Casos de uso e DTOs
- **infrastructure**: Implementações técnicas (HTTP, SQS, persistência)

### Compilação

Para compilar as aplicações individualmente:

```bash
# Compilar Webapp
cd webapp
mvn clean package

# Compilar Scheduler
cd scheduler
mvn clean package
```

## Troubleshooting

### Problemas Comuns

1. **Serviços não iniciam**
   - Verifique se as portas não estão em uso
   - Verifique os logs com `docker-compose logs`

2. **Erro de conexão com banco**
   - Verifique se os serviços MySQL estão rodando
   - Confirme as credenciais nos arquivos de configuração

3. **Erro nas filas SQS**
   - Verifique se o LocalStack está rodando
   - Confirme se as filas foram criadas corretamente

## Contribuição

1. Faça um Fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Licença

Este projeto está sob a licença [MIT](https://opensource.org/licenses/MIT). 