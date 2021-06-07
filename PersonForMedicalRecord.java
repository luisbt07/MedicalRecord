/*

Esta classe representa um PAR CHAVE VALOR (PCV) 
para uma entidade Pessoa. Seu objetivo é representar
uma entrada de índice. 

Esse índice será secundário e indireto, baseado no
email de uma pessoa. Ao fazermos a busca por pessoa,
ele retornará o ID dessa pessoa, para que esse ID
possa ser buscado em um índice direto (que não é
apresentado neste projeto)

Um índice direto de ID precisaria ser criado por meio
de outra classe, cujos dados fossem um int para o ID
e um long para o endereço
 
Implementado pelo Prof. Marcos Kutova
v1.0 - 2021
 
*/

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PersonForMedicalRecord implements aed3.RegistroHashExtensivel<PersonForMedicalRecord> {

  String name;//45
  String birthDate;//10
  String sex;//9
  Long cpf;//8
  int id;// 4
  short cpfSize = 11;
  short maxSize = 76;

  public PersonForMedicalRecord() {
    name = null;
    birthDate = null;
    sex = null;
    cpf = 0L;
  }

  public PersonForMedicalRecord(String name, String birthDate, String sex, Long cpf, int id) {
    try {
      this.name = name;
      this.birthDate = birthDate;
      this.sex = sex;
      this.cpf = cpf;
      this.id = id;
      if (String.valueOf(cpf).length() != cpfSize)
        throw new Exception("Número de caracteres do cpf está incorreto o dado não será inserido");
    } catch (Exception ec) {
      ec.printStackTrace();
    }
  }

  @Override
  public int hashCode() {
    return this.cpf.hashCode();
  }

  public short size() {
    return this.maxSize;
  }

  public String toString() {
    return "\nNome: " + this.name +"\nData de nascimento: " + this.birthDate +"\nSexo: " + this.sex +"\n CPF: " + this.cpf + ";" + this.id;
  }

  public byte[] toByteArray() throws IOException {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    DataOutputStream dOutputStream = new DataOutputStream(byteArrayOutputStream);
    dOutputStream.writeUTF(name);
    dOutputStream.writeUTF(birthDate);
    dOutputStream.writeUTF(sex);
    dOutputStream.writeLong(cpf);
    dOutputStream.writeInt(id);
    byte[] bs = byteArrayOutputStream.toByteArray();
    byte[] bs2 = new byte[maxSize];
    for (int i = 0; i < maxSize; i++)//Prepara um vetor de bytes do tamanho máximo e insere espaços nele
      bs2[i] = ' ';
    for (int i = 0; i < bs.length && i < maxSize; i++)//Insere os dados reais nesse vetor.
      bs2[i] = bs[i];
    return bs2;
  }

  public void fromByteArray(byte[] byteArray) throws IOException { //Converter bytes para objeto
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);// Lê de um vetor de bytes
    DataInputStream dInputStream = new DataInputStream(byteArrayInputStream);
    name = dInputStream.readUTF();
    birthDate =dInputStream.readUTF();
    sex =  dInputStream.readUTF();
    cpf = dInputStream.readLong();
    this.id = dInputStream.readInt();
  }

}