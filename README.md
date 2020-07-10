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


## SYSTEM ARCHITECTURE
The architecture of Bolt allows concurrent executions of MapReduce tasks on a large structured P2P distributed system. Bolt is a framework composed of two main components, *client* and *resource manager*, for data stagging and orchestrating of MapReduce tasks (see Figure below). 

<p align="center">
<img width="500" alt="Framework Arechitecture" src="https://user-images.githubusercontent.com/40745827/87094924-03088c80-c1fd-11ea-85b5-84c59bd631b6.png">
</p>

The **client** can be seen as an interface that enables the users to store input data for their jobs and submitting MapReduce jobs for parallel execution on the system’s nodes. 
- To stage dataset into the system, the client organizes the files composing the whole dataset into groups with approximately the same sizes and then uses DHT to stage them to nodes. 
- Also, the client receives MapReduce jobs from the users, creates map and reduce tasks for each received job, and submits them to the distributed system for parallel execution. 
- The client uses the chunks' names to identify their locations for submitting the map tasks to the hosted nodes to gain data locality and balanced workload.
- Bolt relies on a novel mechanism to generate the proper keys to reduce tasks in a deterministic way. This mechanism allows clients to assign the reduce tasks to a distributed system with a high probability of balancing distribution. 


The **resource manager** runs on each node in the distributed system to store data on local media and execute tasks of different jobs on locally available resources in an efficient way. 
- Given a data chunk key, the resource manager can transparently feed chunk's records to the assigned map task for processing.
- Each resource manager receives MapReduce tasks and executes them in a thread pool to maximize the parallelism degree and resource utilization on the hosted machine.
- The resource managers employ the novel mechanism that determine where the reduce tasks are running to send the intermediate outputs to them and allow the communications between map and reduce tasks of the same job.

## The Algolirthm that generates the keys for reducers 

<p align="center">
<img width="500" alt="Reducer Keys" src="https://user-images.githubusercontent.com/40745827/87099935-49afb400-c208-11ea-92b2-daf11ac45d98.png">
</p>

