# Bolt: A Framework for Orchestration of MapReduce Tasks Using Distributed Hash Tables
Developed a framework, *Bolt*, for executing MapReduce jobs
in a structured peer-to-peer system (P2P) based on distributed
hash tables (DHTs).  This required us to address issues relating to data dispersion, placements, execution of map and reduce tasks, interactions and data exchanges between the Map and Reduce phases while ensuring that decisions are made in a decentralized and deterministic fashion. 
The framework relies on distributed hash tables (DHTs) to disperse data and orchestrate map and reduce tasks over the collection of machines.

The reducer, mapper, and combiner processing functionality needs to be specified during job submission. Like the MapReduce framework, the number of mappers is determined based on the number of chunks associated with the file. However, the developer must specify the number of reducers. 
To ensure data locality during processing maps, tasks are launched on nodes that hold the data blocks. However, in our case, there are not any control messages being sent to the Master (or Namenode) to retrieve the location of the files. Once the metadata for a file is retrieved, our framework launches map tasks on DHT nodes responsible for the cryptographic hash of the filename, chunk number tuple. 

## Features
- Ensuring the effective distribution of datasets. Since the files being processed are large, it is important to ensure that the distribution of a file facilitates concurrent processing. The distribution scheme must also ensures that the storage loads are effectively dispersed, even in cases where the underlying files are vastly different sizes.
- Decentralized orchestration of Map and Reduce tasks.
DHTs are highly decentralized, orchestration frameworks designed over DHTs must be decentralized as well. Furthermore, centralized components introduce hotspots and points of irrecoverable failures.
- Ensuring fast completion times. The choice of the underlying DHT-based storage subsystem that is completely decentralized should not introduce orchestration overheads that preclude timely completion of jobs.
- Minimizing garbage collection overhead  is achieved by reusing of objects instead of creating new ones for new data.
