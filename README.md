## **CACHE SIMULATOR**

This simulator, reads params.txt files under test1 or test2 folders as input parameters,
and also hex addresses and commands in "aligned.trace" file

## **Indexing**

In order to do indexing, I calculated Set Index, Tag and Offset bits.
generateTag method under Cache class, operates that operation.

Example:

Hex Address = bfedde0c

LineSize = 4

numSets = 4

- We convert hexAddress in input to binary => 10111111111011011101111000001100
- First, we should calculate how many rightmost bits will be offset bits
Formula of that is:
  Count of offset bits = `log(lineSize)/log2 = 2 => last 2 bits = 00`
  
According to this result, rightmost 2 bits (which is 00), are our offset bits.

- After that, we should find out setIndex bits which is righ most bits after offset bits

Count of Set Index bits is calculated by `log(numSets)/log(2) = 2`
According to this formula, right most 2 bits after offset bits are our setIndex bits which is 11

`Set Index bits = 11`

And remaining bits are our tag bits = 1011111111101101110111100000

Then, we convert those setIndex and tag binary values to the decimal.
And we use this results to indexing sets and tagging lines(blocks)

According to this:
* Set Index = 3
* Generated Tag = 201252320

### **DEVELOPMENT**

We use setIndex and tag values that we found out above, in order to index cache

```java
private final Map<Integer, CacheSet> cacheSets;
```

Above Map class, represents cache. Integer key in here, represents the setIndex that we calculated via generateTag method

The reason of using Map in here is, it just seemed like easy to access and change values with key.
The Value CacheSet class represents each set of cache

Then we use below method to insert addresses (in aligned.trace file) into the cache

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

        cacheSet.insertInSet(tag, this.replacementPolicy, command, writeAlgorithm);
    }
```

In here, we calculate setIndex and tag with generateTag method

Then we check, if there is a set with that setIndex in our Map, if there is no set, we just initialize a new one.
If there is a set, we use existing one

Inserting into the lines(blocks) in set:

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

In this method, we insert addresses into the lines due to tag values that we just generated.
We use Map again as a data structure, key is tag and value is Line class wihch contains other necessary parameters
of line (isValid, isDirty, frequency, recency)

To short summarize, in first if clause, if lineMap's size is not equal to desired block(line) count (numWays),
and there is no line(block) with desired tag, we simply create new Line and put it into the set
. And we increment miss counts in here

If set is full but there is no line that belongs to the input tag,
we remove one of the existing lines depends on ReplacementPolicy in input (either LRU or LFU)
and we replace it with the line with new tag. And we also increment miss in here

In removing process, we just gather the line with least frequency or recency (depends on input Replacement Policy)
and remove it from the lineMap

If there is a line with input tag, it means this is a hit. In this case,
if input Command is Store, we decide if we should increment write counts to the main memory, depends on input Write Algorithm

## **Running Simulator**

One can run the simulator by using main method in Simulator class
```java
    public static void main(String[] args) throws IOException {
        runSimulatorAndGenerateResults("test1");
    }
```

If we provide "test1" as an input, it will run the simulator for "params.txt" file under src/main/resources/test1

Else if we provide "test2", it will use src/main/resources/test2

Other than that, if project is builded, jar file should be generated under
/out/artifacts/CacheSimulator_jar

One can run the simulator by using that jar file also.

- In order to run the program with the "params.txt" file under src/main/resources/test1
```shell
java -jar CacheSimulator.jar test1
```

- In order to run the program with the "params.txt" file under src/main/resources/test2
```shell
java -jar CacheSimulator.jar test2
```
Results will be generated both in command line, and existing directory as a txt file

If no input is provided as a test file, "test1" will be used as a default


## **BUG NOTICE:**
```java
    public static void main(String[] args) throws IOException {
        runSimulatorAndGenerateResults("test1");
        runSimulatorAndGenerateResults("test2");
    }
```
If you try to run the program like above, it will generate wrong results. It is probably related with Statistics file that I use, so this will be fixed.









