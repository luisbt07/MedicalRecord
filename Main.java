/*
TESTE DE TABELA HASH EXTENSÍVEL

Este programa principal serve para demonstrar o uso
da tabela hash extensível como um índice indireto.
Aqui, cada elemento do índice será composto pelo par
(email, ID) representado por meio de um objeto da classe
pcvPessoa (PCV = par chave valor).

Para funcionamento como índice direto, precisaríamos de 
mais uma classe que contivesse o par (ID, endereço).
Mas isso fica por sua conta ;)

Implementado pelo Prof. Marcos Kutova
v1.1 - 2021
*/

import java.util.Scanner;
import java.io.File;
import aed3.HashExtensivel;

public class Main {

  // Método principal apenas para testes
  public static void main(String[] args) {

    HashExtensivel<PersonForMedicalRecord> he;
    Scanner console = new Scanner(System.in);

    try {
      File directory = new File("dados");
      if (!directory.exists())
        directory.mkdir(); // create directory
      he = new HashExtensivel<>(PersonForMedicalRecord.class.getConstructor(), 3, "dados/PersonForMedicalRecord.hash_directory.db",
          "dados/PersonForMedicalRecord.hash_bucket.db");

      int opcao;
      do {
        System.out.println("\n\n-------------------------------");
        System.out.println("              MENU");
        System.out.println("-------------------------------");
        System.out.println("1 - Inserir");
        System.out.println("2 - Buscar");
        System.out.println("3 - Excluir");
        System.out.println("4 - Imprimir");
        System.out.println("0 - Sair");
        try {
          opcao = Integer.parseInt(console.nextLine());
        } catch (NumberFormatException e) {
          opcao = -1;
        }

        switch (opcao) {
        case 1: {
          System.out.println("\nINCLUSÃO");
          System.out.print("Name: ");
          String name = console.nextLine();
          System.out.print("Birth Date: ");
          String birthDate = (console.nextLine());
          System.out.print("SEX: ");
          String sex = (console.nextLine());
          System.out.print("CPF: ");
          Long cpf = Long.parseLong(console.nextLine());
          System.out.print("ID: ");
          int id = Integer.parseInt(console.nextLine());
          he.create(new PersonForMedicalRecord(name,birthDate, sex, cpf, id));
          he.print();
        }
          break;
        case 2: {
          System.out.println("\nBUSCA");

          System.out.print("CPF: ");
          Long cpf = Long.parseLong(console.nextLine());
          System.out.print("Dados: " + he.read(cpf.hashCode()));
        }
          break;
        case 3: {
          System.out.println("\nEXCLUSÃO");

          System.out.print("CPF: ");
          Long cpf = Long.parseLong(console.nextLine());
          he.delete(cpf.hashCode());
          he.print();
        }
          break;
        case 4: {
          he.print();
        }
          break;
        case 0:
          break;
        default:
          System.out.println("Opção inválida");
        }
      } while (opcao != 0);

    } catch (Exception e) {
      e.printStackTrace();
    }
    console.close();
  }
}