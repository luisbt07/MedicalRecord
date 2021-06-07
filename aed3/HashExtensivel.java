/*
TABELA HASH EXTENSÍVEL

Os nomes dos métodos foram mantidos em inglês
apenas para manter a coerência com o resto da
disciplina:
- boolean create(T elemento)
- long read(int hashcode)
- boolean update(T novoElemento)   //  a chave deve ser a mesma
- boolean delete(int hashcode)

Implementado pelo Prof. Marcos Kutova
v1.1 - 2021
*/
package aed3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.lang.reflect.Constructor;

public class HashExtensivel<T extends RegistroHashExtensivel<T>> {

  String directoryFileName;
  String bucketFileName;
  RandomAccessFile directoryFile;
  RandomAccessFile bucketFile;
  int amountDataPerBucket;
  Directory directory;
  Constructor<T> constructor;

  public class Bucket {

    Constructor<T> constructor;
    byte localDepth; // profundidade local do cesto
    short quantidade; // quantidade de pares presentes no cesto
    short quantidadeMaxima; // quantidade máxima de pares que o cesto pode conter
    ArrayList<T> elementos; // sequência de elementos armazenados
    short bytesPorElemento; // tamanho fixo de cada elemento em bytes
    short bytesPorCesto; // tamanho fixo do cesto em bytes

    public Bucket(Constructor<T> constructor, int qtdmax) throws Exception {
      this(constructor, qtdmax, 0);
    }

    public Bucket(Constructor<T> constructor, int qtdmax, int localDepth) throws Exception {
      this.constructor = constructor;
      if (qtdmax > 32767)
        throw new Exception("Quantidade máxima de 32.767 elementos");
      if (localDepth > 127)
        throw new Exception("Profundidade local máxima de 127 bits");
      this.localDepth = (byte) localDepth;
      quantidade = 0;
      quantidadeMaxima = (short) qtdmax;
      elementos = new ArrayList<>(quantidadeMaxima);
      bytesPorElemento = constructor.newInstance().size();
      bytesPorCesto = (short) (bytesPorElemento * quantidadeMaxima + 3);
    }

    public byte[] toByteArray() throws Exception {//Bucket
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream dOutputStream = new DataOutputStream(byteArrayOutputStream);
      dOutputStream.writeByte(localDepth);
      dOutputStream.writeShort(quantidade);
      int i = 0;
      while (i < quantidade) {
        dOutputStream.write(elementos.get(i).toByteArray());
        i++;
      }
      byte[] vazio = new byte[bytesPorElemento];
      while (i < quantidadeMaxima) {
        dOutputStream.write(vazio);
        i++;
      }
      return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] byteArrayBucket) throws Exception { //Recebe um vetor de bytes de um cesto para serem transformados em objeto
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayBucket); //Lê de um vetor de bytes
      DataInputStream dInputStream = new DataInputStream(byteArrayInputStream);
      localDepth = dInputStream.readByte();
      quantidade = dInputStream.readShort();
      int i = 0;
      elementos = new ArrayList<>(quantidadeMaxima);
      byte[] dados = new byte[bytesPorElemento];
      T elem;
      while (i < quantidadeMaxima /*- quantidade && quantidade != 0 */) {
        dInputStream.read(dados);
        elem = constructor.newInstance();
        elem.fromByteArray(dados); //Pega os dados do bucket e transforma em objetos do tipo PersonForMedicalRecord
        elementos.add(elem);
        i++;
      }
    }

    public boolean create(T elem) {//Cria de fato o elemento que é inserido no console
      if (full())
        return false;
      int i = quantidade - 1;
      while (i >= 0 && elem.hashCode() < elementos.get(i).hashCode())
        i--;
      elementos.add(i + 1, elem);
      quantidade++;
      return true;
    }

    public T read(int chave) {
      if (empty())
        return null;
      int i = 0;
      while (i < quantidade && chave > elementos.get(i).hashCode())
        i++;
      if (i < quantidade && chave == elementos.get(i).hashCode())
        return elementos.get(i);
      else
        return null;
    }

    public boolean update(T elem) {
      if (empty())
        return false;
      int i = 0;
      while (i < quantidade && elem.hashCode() > elementos.get(i).hashCode())
        i++;
      if (i < quantidade && elem.hashCode() == elementos.get(i).hashCode()) {
        elementos.set(i, elem);
        return true;
      } else
        return false;
    }

    public boolean delete(int chave) {
      if (empty())
        return false;
      int i = 0;
      while (i < quantidade && chave > elementos.get(i).hashCode())
        i++;
      if (chave == elementos.get(i).hashCode()) {
        elementos.remove(i);
        quantidade--;
        return true;
      } else
        return false;
    }

    public boolean empty() {
      return quantidade == 0;
    }

    public boolean full() {
      return quantidade == quantidadeMaxima;
    }

    public String toString() { //Printa os elementos alocados no bucket
      String localDepthAndQuantity = "Profundidade Local: " + localDepth + "\nQuantidade: " + quantidade + "\n| ";
      int i = 0;
      while (i < quantidade) {
        localDepthAndQuantity += elementos.get(i).toString() + " | ";
        i++;
      }
      while (i < quantidadeMaxima) {
        localDepthAndQuantity += "- | ";
        i++;
      }
      return localDepthAndQuantity;
    }

    public int size() {
      return bytesPorCesto;
    }

  }

  protected class Directory {

    byte globalDepth;
    long[] address;

    public Directory() {
      globalDepth = 0;
      address = new long[1];
      address[0] = 0;
    }

    public boolean updateAddress(int p, long e) {
      if (p > Math.pow(2, globalDepth))
        return false;
      address[p] = e;
      return true;
    }

    public byte[] toByteArray() throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream dOutputStream = new DataOutputStream(byteArrayOutputStream);
      dOutputStream.writeByte(globalDepth);
      int quantidade = (int) Math.pow(2, globalDepth);
      int i = 0;
      while (i < quantidade) {
        dOutputStream.writeLong(address[i]);
        i++;
      }
      return byteArrayOutputStream.toByteArray();
    }

    public void fromByteArray(byte[] byteArrayDirectory) throws IOException { //
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayDirectory);
      DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
      globalDepth = dataInputStream.readByte();
      int quantidade = (int) Math.pow(2, globalDepth);
      address = new long[quantidade];
      int i = 0;
      while (i < quantidade) {
        address[i] = dataInputStream.readLong();
        i++;
      }
    }

    public String toString() {
      String s = "\nProfundidade global: " + globalDepth;
      int i = 0;
      int quantidade = (int) Math.pow(2, globalDepth);
      while (i < quantidade) {
        s += "\n" + i + ": " + address[i];
        i++;
      }
      return s;
    }

    protected long address(int p) {
      if (p > Math.pow(2, globalDepth))
        return -1;
      return address[p];
    }

    protected boolean duplicate() {
      if (globalDepth == 127)
        return false;
      globalDepth++;
      int q1 = (int) Math.pow(2, globalDepth - 1);
      int q2 = (int) Math.pow(2, globalDepth);
      long[] newAddress = new long[q2];
      int i = 0;
      while (i < q1) {
        newAddress[i] = address[i];
        i++;
      }
      while (i < q2) {
        newAddress[i] = address[i - q1];
        i++;
      }
      address = newAddress;
      return true;
    }

    // Para efeito de determinar o cesto em que o elemento deve ser inserido,
    // só serão considerados valores absolutos da chave.
    protected int hash(int chave) {
      return Math.abs(chave) % (int) Math.pow(2, globalDepth);
    }

    protected int hash2(int chave, int pl) { // cálculo do hash para uma dada profundidade local
      return Math.abs(chave) % (int) Math.pow(2, pl);
    }

  }

  public HashExtensivel(Constructor<T> constructor, int n, String directoryFileName, String bucketFileName) throws Exception {
    this.constructor = constructor;
    amountDataPerBucket = n;
    this.directoryFileName = directoryFileName;
    this.bucketFileName = bucketFileName;

    directoryFile = new RandomAccessFile(directoryFileName, "rw");
    bucketFile = new RandomAccessFile(bucketFileName, "rw");

    // Se o diretório ou os cestos estiverem vazios, cria um novo diretório e lista
    // de cestos
    if (directoryFile.length() == 0 || bucketFile.length() == 0) {

      // Cria um novo diretório, com profundidade de 0 bits (1 único elemento)
      directory = new Directory();
      byte[] bd = directory.toByteArray();
      directoryFile.write(bd);

      // Cria um cesto vazio, já apontado pelo único elemento do diretório
      Bucket bucket = new Bucket(constructor, amountDataPerBucket);
      bd = bucket.toByteArray();
      bucketFile.seek(0);
      bucketFile.write(bd);
    }
  }

  public boolean create(T elem) throws Exception {

    // Carrega o diretório
    byte[] byteArrayDirectory = new byte[(int) directoryFile.length()]; //Cria um vetor de bytes do tamanho do diretorio
    directoryFile.seek(0);//Posição inicial do diretorio
    directoryFile.read(byteArrayDirectory);//Lê tod-o o diretório no byteArrayDirectory
    directory = new Directory();
    directory.fromByteArray(byteArrayDirectory);

    // Identifica a hash do diretório, (endereço do diretório)
    int i = directory.hash(elem.hashCode());

    // Recupera o cesto
    long enderecoCesto = directory.address(i); //recupera o endereço do cesto
    Bucket bucket = new Bucket(constructor, amountDataPerBucket);
    byte[] byteArray = new byte[bucket.size()];
    bucketFile.seek(enderecoCesto);//direciona pontei para o endereço do cesto
    bucketFile.read(byteArray);//Lê os dados do cesto e joga em um byteArray
    bucket.fromByteArray(byteArray);//Transforma os dados do byte array em objeto

    // Testa se a chave já não existe no cesto
    if (bucket.read(elem.hashCode()) != null)
      throw new Exception("Elemento já existe");

    // Testa se o cesto já não está cheio
    // Se não estiver, create o par de chave e dado
    if (!bucket.full()) {
      // Insere a chave no cesto e o atualiza
      bucket.create(elem);
      bucketFile.seek(enderecoCesto);
      bucketFile.write(bucket.toByteArray());
      return true;
    }

    // duplicate the directory
    byte localDepth = bucket.localDepth;
    if (localDepth >= directory.globalDepth)
      directory.duplicate();
    byte globalDepth = directory.globalDepth;

    // Cria os novos cestos, com os seus dados no arquivo de cestos
    Bucket bucket1 = new Bucket(constructor, amountDataPerBucket, localDepth + 1);
    bucketFile.seek(enderecoCesto);
    bucketFile.write(bucket1.toByteArray());

    Bucket bucket2 = new Bucket(constructor, amountDataPerBucket, localDepth + 1);
    long newAddress = bucketFile.length();
    bucketFile.seek(newAddress);
    bucketFile.write(bucket2.toByteArray());

    // Atualiza os dados no diretório
    int startPoint = directory.hash2(elem.hashCode(), bucket.localDepth);
    int deslocamento = (int) Math.pow(2, localDepth);
    int max = (int) Math.pow(2, globalDepth);
    boolean troca = false;
    for (int j = startPoint; j < max; j += deslocamento) {
      if (troca)
        directory.updateAddress(j, newAddress);
      troca = !troca;
    }

    // Atualiza o arquivo do diretório
    byteArrayDirectory = directory.toByteArray();
    directoryFile.seek(0);
    directoryFile.write(byteArrayDirectory);

    // Reinsere as chaves
    for (int j = 0; j < bucket.quantidade; j++) {
      create(bucket.elementos.get(j));
    }
    create(elem);
    return false;

  }

  public T read(int chave) throws Exception {

    // Carrega o diretório
    byte[] bd = new byte[(int) directoryFile.length()];
    directoryFile.seek(0);
    directoryFile.read(bd);
    directory = new Directory();
    directory.fromByteArray(bd);

    // Identifica a hash do diretório,
    int i = directory.hash(chave);

    // Recupera o cesto
    long enderecoCesto = directory.address(i);
    Bucket c = new Bucket(constructor, amountDataPerBucket);
    byte[] ba = new byte[c.size()];
    bucketFile.seek(enderecoCesto);
    bucketFile.read(ba);
    c.fromByteArray(ba);

    return c.read(chave);
  }

  public boolean update(T elem) throws Exception {

    // Carrega o diretório
    byte[] bd = new byte[(int) directoryFile.length()];
    directoryFile.seek(0);
    directoryFile.read(bd);
    directory = new Directory();
    directory.fromByteArray(bd);

    // Identifica a hash do diretório,
    int i = directory.hash(elem.hashCode());

    // Recupera o cesto
    long enderecoCesto = directory.address(i);
    Bucket c = new Bucket(constructor, amountDataPerBucket);
    byte[] ba = new byte[c.size()];
    bucketFile.seek(enderecoCesto);
    bucketFile.read(ba);
    c.fromByteArray(ba);

    // atualiza o dado
    if (!c.update(elem))
      return false;

    // Atualiza o cesto
    bucketFile.seek(enderecoCesto);
    bucketFile.write(c.toByteArray());
    return true;

  }

  public boolean delete(int chave) throws Exception {

    // Carrega o diretório
    byte[] bd = new byte[(int) directoryFile.length()];
    directoryFile.seek(0);
    directoryFile.read(bd);
    directory = new Directory();
    directory.fromByteArray(bd);

    // Identifica a hash do diretório,
    int i = directory.hash(chave);

    // Recupera o cesto
    long enderecoCesto = directory.address(i);
    Bucket c = new Bucket(constructor, amountDataPerBucket);
    byte[] ba = new byte[c.size()];
    bucketFile.seek(enderecoCesto);
    bucketFile.read(ba);
    c.fromByteArray(ba);

    // delete a chave
    if (!c.delete(chave))
      return false;

    // Atualiza o cesto
    bucketFile.seek(enderecoCesto);
    bucketFile.write(c.toByteArray());
    return true;
  }

  public void print() {
    try {
      byte[] byteArrayDirectory = new byte[(int) directoryFile.length()];
      directoryFile.seek(0);
      directoryFile.read(byteArrayDirectory);//Lê todos os dados do diretorio
      directory = new Directory();
      directory.fromByteArray(byteArrayDirectory); //Converte todos os dados do diretorio
      System.out.println("\nDIRETÓRIO ------------------");
      System.out.println(directory);

      System.out.println("\nCESTOS ---------------------");
      bucketFile.seek(0);
      while (bucketFile.getFilePointer() != bucketFile.length()) {
        System.out.println("Endereço: " + bucketFile.getFilePointer());
        Bucket bucket = new Bucket(constructor, amountDataPerBucket);
        byte[] ba = new byte[bucket.size()];
        bucketFile.read(ba);
        bucket.fromByteArray(ba);
        System.out.println(bucket + "\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
