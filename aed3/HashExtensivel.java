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

import java.io.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

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
        short quantity; // quantidade de pares presentes no cesto
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
            quantity = 0;
            quantidadeMaxima = (short) qtdmax;
            elementos = new ArrayList<>(quantidadeMaxima);
            bytesPorElemento = constructor.newInstance().size();
            bytesPorCesto = (short) (bytesPorElemento * quantidadeMaxima + 3);
        }

        public byte[] toByteArrayBucket() throws IOException {//Bucket
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            DataOutputStream dOutputStream = new DataOutputStream(byteArrayOutputStream);
            dOutputStream.writeByte(localDepth);
            dOutputStream.writeShort(quantity);
            int i = 0;
            while (i < quantity) {
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
            quantity = dInputStream.readShort();
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
            int i = quantity - 1;
            while (i >= 0 && elem.hashCode() < elementos.get(i).hashCode())
                i--;
            elementos.add(i + 1, elem);
            quantity++;
            return true;
        }

        public T read(int chave) {
            if (empty())
                return null;
            int i = 0;
            while (i < quantity && chave > elementos.get(i).hashCode())
                i++;
            if (i < quantity && chave == elementos.get(i).hashCode())
                return elementos.get(i);
            else
                return null;
        }

        public boolean update(T elem) {
            if (empty())
                return false;
            int i = 0;
            while (i < quantity && elem.hashCode() > elementos.get(i).hashCode())
                i++;
            if (i < quantity && elem.hashCode() == elementos.get(i).hashCode()) {
                elementos.set(i, elem);
                return true;
            } else
                return false;
        }

        public boolean delete(int chave) {
            if (empty())
                return false;
            int i = 0;
            while (i < quantity && chave > elementos.get(i).hashCode())
                i++;
            if (chave == elementos.get(i).hashCode()) {
                elementos.remove(i);
                quantity--;
                return true;
            } else
                return false;
        }

        public boolean empty() {
            return quantity == 0;
        }

        public boolean full() {
            return quantity == quantidadeMaxima;
        }

        public String toString() { //Printa os elementos alocados no bucket
            StringBuilder localDepthAndQuantity = new StringBuilder("Profundidade Local: " + localDepth + "\nQuantidade: " + quantity + "\n| ");
            int i = 0;
            while (i < quantity) {
                localDepthAndQuantity.append(elementos.get(i).toString()).append(" | ");
                i++;
            }
            while (i < quantidadeMaxima) {
                localDepthAndQuantity.append("- | ");
                i++;
            }
            return localDepthAndQuantity.toString();
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
            int quantity;
            quantity = (int) Math.pow(2, globalDepth);
            address = new long[quantity];
            int pos = 0;
            while (pos < quantity) {
                address[pos] = dataInputStream.readLong();
                pos++;
            }
        }

        public String toString() {
            StringBuilder globalDepthAndValues  = new StringBuilder("\nProfundidade global: " + globalDepth);
            int pos = 0;
            int quantity = (int) Math.pow(2, globalDepth);
            while (pos < quantity) {
                globalDepthAndValues.append("\n").append(pos).append(": ").append(address[pos]);
                pos++;
            }
            return globalDepthAndValues.toString();
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
        protected int hash(int key) {
            return Math.abs(key) % (int) Math.pow(2, globalDepth);
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
            byte[] byteArrayDirectory = directory.toByteArray();
            directoryFile.write(byteArrayDirectory);

            // Cria um cesto vazio, já apontado pelo único elemento do diretório
            Bucket bucket = new Bucket(constructor, amountDataPerBucket);
            byteArrayDirectory = bucket.toByteArrayBucket();
            bucketFile.seek(0);
            bucketFile.write(byteArrayDirectory);
        }
    }

    public boolean create(T elem) throws Exception {

        // Carrega o diretório
        byte[] byteArrayDirectory = ChargeDirectory();

        // Identifica a hash do diretório, (endereço do diretório)
        int index = directory.hash(elem.hashCode());

        // Recupera o cesto
        long bucketAddress = directory.address(index);
        Bucket bucket = new Bucket(constructor, amountDataPerBucket);
        ChargeBucket(bucketAddress, bucket);

        // Testa se a chave já não existe no cesto
        if (bucket.read(elem.hashCode()) != null)
            throw new Exception("Elemento já existe");

        // Testa se o cesto já não está cheio
        // Se não estiver, create o par de chave e dado
        if (!bucket.full()) {
            // Insere a chave no cesto e o atualiza
            bucket.create(elem);
            bucketFile.seek(bucketAddress);
            bucketFile.write(bucket.toByteArrayBucket());
            return true;
        }

        // duplicate the directory
        byte localDepth = bucket.localDepth;
        if (localDepth >= directory.globalDepth)
            directory.duplicate();
        byte globalDepth = directory.globalDepth;

        // Cria os novos cestos, com os seus dados no arquivo de cestos
        Bucket bucket1 = new Bucket(constructor, amountDataPerBucket, localDepth + 1);
        bucketFile.seek(bucketAddress);
        bucketFile.write(bucket1.toByteArrayBucket());

        Bucket bucket2 = new Bucket(constructor, amountDataPerBucket, localDepth + 1);
        long newAddress = bucketFile.length();
        bucketFile.seek(newAddress);
        bucketFile.write(bucket2.toByteArrayBucket());

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
        for (int j = 0; j < bucket.quantity; j++) {
            create(bucket.elementos.get(j));
        }
        create(elem);
        return false;

    }

    public T read(int key) throws Exception {

        // Carrega o diretório
        byte[] byteArrayDirectory = ChargeDirectory();

        int index = directory.hash(key); // Identifica a hash do diretório,

        // Recupera o cesto
        long bucketAddress = directory.address(index);
        Bucket bucket = new Bucket(constructor, amountDataPerBucket);
        ChargeBucket(bucketAddress, bucket);

        return bucket.read(key);
    }

    public boolean update(T elem) throws Exception {

        // Carrega o diretório
        ChargeDirectory();

        // Identifica a hash do diretório,
        int index = directory.hash(elem.hashCode());

        // Recupera o cesto
        long bucketAddress = directory.address(index);
        Bucket bucket = new Bucket(constructor, amountDataPerBucket);
        ChargeBucket(bucketAddress, bucket);

        // atualiza o dado
        if (!bucket.update(elem))
            return false;

        // Atualiza o cesto
        UpdateBucket(bucketAddress, bucket);
        return true;

    }

    public boolean delete(int key) throws Exception {

        // Carrega o diretório
        ChargeDirectory();

        // Identifica a hash do diretório,
        int index = directory.hash(key);

        // Recupera o cesto
        long bucketAddress = directory.address(index);
        Bucket bucket = new Bucket(constructor, amountDataPerBucket);
        ChargeBucket(bucketAddress, bucket);

        // delete a chave
        if (!bucket.delete(key))
            return false;

        // Atualiza o cesto
        UpdateBucket(bucketAddress, bucket);
        return true;
    }

    public void print() {
        try {
            ChargeDirectory();
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

    public byte[] ChargeDirectory() throws IOException {
        byte[] byteArrayDirectory = new byte[(int) directoryFile.length()]; //Cria um vetor de bytes do tamanho do diretorio
        directoryFile.seek(0);//Posição inicial do diretorio
        directoryFile.read(byteArrayDirectory);//Lê tod-o o diretório no byteArrayDirectory
        directory = new Directory();
        directory.fromByteArray(byteArrayDirectory); //Converte todos os dados do diretorio
        return byteArrayDirectory;
    }

    public void ChargeBucket(long bucketAddress, Bucket bucket) throws Exception {
        byte[] byteArrayBucket = new byte[bucket.size()];
        bucketFile.seek(bucketAddress);//direciona pontei para o endereço do cesto
        bucketFile.read(byteArrayBucket);//Lê os dados do cesto e joga em um byteArray
        bucket.fromByteArray(byteArrayBucket);//Transforma os dados do byte array em objeto
    }

    public void UpdateBucket(long bucketAddress, Bucket bucket) throws IOException {
        bucketFile.seek(bucketAddress);
        bucketFile.write(bucket.toByteArrayBucket());
    }
}
