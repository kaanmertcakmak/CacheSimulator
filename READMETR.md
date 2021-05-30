## **CACHE SIMULATOR**

Ön bellek simülatörü,
Bu simülatör test1, test2 dosyaları altındaki params.txt dosyalarındaki parametrelere ve "aligned.trace" dosyasındaki adres erişimlerine göre, ön bellek davranışlarını simüle etmektedir.

## **Indeksleme**

Indeksleme işlemini gerçekleştirirken, Set Index, Tag ve Offset bitlerini hesapladım. 
Bunu, Cache sınıfı altındaki generateTag methoduyla yaptım. Methodun hesaplama şeklini aşağıdaki örnekle anlatacağım.
Örnek:

Hex Address = bfedde0c
LineSize = 4
numSets = 4

Girişteki hex address değerini binary'ye çeviriyoruz => 10111111111011011101111000001100
Öncelikle en sağdaki kaç basamağı offset biti olarak alacağımızı hesaplıyoruz. Bunun formülü aşağıdaki gibidir:

Offset bit sayısı = `log(lineSize)/log2 = 2 => last 2 bits = 00` 

Bu sonuca göre, en sağdaki 2 bit, offset bitlerimiz oluyor.
Sonra, offset bitinden sonraki kaç biti (en sağdaki) setIndex bitleri olarak alacağımızı hesaplıyoruz

Set Index bit sayısı = `log(numSets)/log(2) = 2`. Buna göre, offset bitlerinden sonraki en sağdaki 2 biti de setIndex bitleri olarak alıyoruz.

`Set Index bits = 11` oluyor.

Kalan bitleri de tag bitleri olarak alıyoruz. Son olarak, buradaki setIndex ve tag binary değerlerini, decimal'e çeviriyoruz
ve bu değerleri indeksleme ve etiketleme için kullanıyoruz. 

Buna göre:
* Set index = 3
* Generated Tag = 201252320

sonuçlarını buluyoruz.

### **GELİŞTİRME**

Yukarıda oluşturduğumuz küme indeksi ve etiket değerlerini, cache'i indekslemek için kullanıyoruz.

```java
private final Map<Integer, CacheSet> cacheSets;
```

Yukarıdaki Map tanımı, cache'i temsil etmektedir. Buradaki Integer key değeri, generateTag methoduyla hesapladığımız setIndex değerlerini, indekslemek için kullanmaktadır.
Veri yapısı olarak Map kullanmamın sebebi, key verildiği taktirde istenen değere ulaşmanın daha kolay olmasıdır.
Value olan CacheSet sınıfı ise cache'in bir setini temsil etmektedir. 

Sonrasında aşağıdaki methodlar çağırılarak, aligned.trace dosyasındaki adresler, cache'e yerleştirilmektedir.

```java
    /**
     * Base methods that operates all cache operations
     * @param hexAddress Hex address as an input
     * @param numWays Parameters to decide how many line blocks will be generated
     * @param command Memory command. Can be either LOAD or STORE
     */
    public void insertInCache(String hexAddress, int numWays, Command command) {
        long tag = generateTag(hexAddress);
        int setIndex = CacheParameters.INSTANCE.getSetIndex();
        CacheSet cacheSet = cacheSets.computeIfAbsent(setIndex, c -> {
            CacheSet set = new CacheSet(numWays);
            cacheSets.put(setIndex, set);
            return set;
        });

        cacheSet.insertInSet(tag, hexAddress, this.replacementPolicy, command, writeAlgorithm);
    }
```
Burada yapılan şey, Eğer generateTag methoduyla, input olarak gelen hexAddress için tag ve setIndex değerleri hesaplandıktan sonra,

Bu setIndex için, herhangi bir set olup olmadığı kontrol edilir. Eğer yoksa, yeni bir set oluşturulur ve az önce oluşturduğumuz map'e setIndex kullanılarak eklenir.
Eğer var ise, var olan set'e yerleştirme yapılır.

Son satırda da, az önce hesaplanan tag değeri ve diğer parametreler kullanılarak, insertInSet methodu aracılığıyla set'in içindeki linelara yerleştirme işlemi gerçekleştirilir.

Set içindeki line'lara yerleştirme:

```java
    /**
     * Cache operation method
     * @param tag Tag that will be stored or gathered to the cache
     * @param replacementPolicy Parameter to define we will replace values with LRU or LFU
     * @param command Memory command. Can be either LOAD or STORE
     * @param writeAlgorithm Write algorithm can be either write_back or write_through
     */
    public void insertInSet(long tag, ReplacementPolicy replacementPolicy, Command command, WriteAlgorithm writeAlgorithm) {
        int recency = CacheParameters.INSTANCE.getRecency() + 1;
        Line line = lineMap.get(tag);
        if(lineMap.size() != numWays && Objects.isNull(line)) {
            // When we have lines less than ways count, and the line that we look for is not in set...
            line = insertNewLineToTheCache(tag, recency, command, writeAlgorithm);
            lineMap.put(tag, line);
        } else if (Objects.isNull(line)){
            // line block with key as "tag" equals null, that means we don't have any line with that tag, so we replace it
            line = replaceLineWithRemovedOne(tag, replacementPolicy, command, writeAlgorithm, recency);
            lineMap.put(tag, line);
        } else {
            // Enters this else part when we found what we look for in cache, so it is a hit
            line.setRecency(recency);
            line.incrementFrequency();
            Statistics.INSTANCE.incrementHits();

            if(command.getName().equals(Command.STORE.getName())) {
                if(writeAlgorithm.isEqualTo(WriteAlgorithm.WRITE_THROUGH)) {
                    Statistics.INSTANCE.incrementWriteCountToTheMainMemory();
                    line.setDirty(false);
                } else {
                    line.setDirty(true);
                }
            }

        }
    }
```

Bu methodda, set'in içindeki line'lara, tag'e göre adresler yerleştirilmiştir.
Veri yapısı olarak yine Map kullandım (lineMap), key değeri olarak tag, value değeri olarak da line'daki diğer parametreleri (isValid, isDirty, frequency, recency)
değerlerini tutan Line sınıfı kullanılmıştır.

Kısaca anlatmak gerekirse, ilk if kısmında, eğer lineMap'in boyutu (yani bir satırdaki line bloğu sayısı)
istenen blok sayısı değerine (numWays) eşit değilse, ve ulaşmak istediğimiz tag'i içeren bir line, o satırda yoksa, basitçe yeni bir Line yaratılır ve o Set'in içine yerleştirilir.

Eğer set tam kapasitesine (numWays) ulaşmışsa, ancak input olan tag'e ait line bulunamamışsa, var olan line'lardan birini çıkarıyoruz.
(LRU ya da LFU göre) Ve yerine input olarak gelen tag'i kullanarak yeni bir line ekliyoruz.
Buradaki çıkarma işleminde, veri yapısı olarak map kullanmanın yararını görüyoruz.
Frequency'si ya da recency'si en az olan line'ın tag'ini bulduktan sonra, bu tag'i key olarak kullanarak, line'ı map'ten çıkartıyoruz.

Eğer aradığımız tag'e ait bir line varsa, bu isabet demektir. Dolayısıyla bu kısımda da isabet sayısını arttırıyoruz ve eğer komutumuz Store komutuysa,
Input olarak gelen yazma algoritmasına göre, ana hafızasına yazma sayısı arttırıp arttırmayacağımıza karar veriyoruz.

## **Programı Çalıştırmak**

Programı Simulator.java sınıfındaki main methodunu run ederek çalıştırıyoruz.

```java
    public static void main(String[] args) throws IOException {
        runSimulatorAndGenerateResults("test1");
    }
```

Yukarıdaki kod blogunu test1 için çalıştırdığımızda, src/main/resources/test1 dosyası altındaki parametreleri alarak,
test2 için çalıştırdığımızda da, test2 dosyası altındaki parametreleri alarak çalıştırır.

Bunun dışında, projeyi build ettiğinizde /out/artifacts/CacheSimulator_jar pathinin altında, CacheSimulator.jar dosyasını göreceksiniz.

Bu dosya kullanılarak da program çalıştırılabilir.

- Programı test1 dosyasındaki parametrelere göre çalıştırabilmek için
```shell
java -jar CacheSimulator.jar test1
```

Sonuçlar out/artifacts/CacheSimulator_jar dosyası altında "test1-Final-results.txt" ve "test1-Warmup-results.txt"

- Programı test2 dosyasındaki parametrelere göre çalıştırabilmek için
```shell
java -jar CacheSimulator.jar test2
```
Sonuçlar out/artifacts/CacheSimulator_jar dosyası altında "test1-Final-results.txt" ve "test1-Warmup-results.txt"

Eğer test1 veya test2 yerine herhangi bir input girilmezse, default olarak test1 için çalıştırılacaktır

## **UYARI:**
```java
    public static void main(String[] args) throws IOException {
        runSimulatorAndGenerateResults("test1");
        runSimulatorAndGenerateResults("test2");
    }
```

Yukarıdaki gibi alt alta çalıştırmayı denediğimizde, sonuçlar yanlış çıkıyor. İstatistikleri tuttuğum Statistics sınıfındaki bir hata sebebiyle olduğunu düşünüyorum.
Düzelteceğim









