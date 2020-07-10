# Bolt: A Framework for Orchestration of MapReduce Tasks Using Distributed Hash Tables
Developed a framework, Bolt, for executing MapReduce jobs in a structured peer-to-peer system (P2P) based on distributed hash tables (DHTs). This required us to address issues relating to data dispersion and placement, scheduling and execution of map and reduce tasks, and interactions and data exchanges between the Map and Reduce phases while ensuring that decisions are made in a decentralized and deterministic fashion. The framework mainly relies on distributed hash tables (DHTs) to disperse data and orchestrate map and reduce tasks over the collection of machines in a decentralized manner. Bolt generates keys that are hashed using the consistent hash function MD5 to gain a good balancing distribution of data chunks and MapReduce tasks. 

### Staging the datasets
The given dataset is divided into chunks of the same size (a default chunk size of 128 MB). For each generated chunk, Bolt creates a name that has. the form  "datasetName_serialNumber. For example, if *atlas* dataset is divided into 10 chunks, then their names will be  *atlas_0*,  *atlas_1*,  *atlas_2*, ...,  *atlas_9*. By staging the dataset chunks, the framework uses MD5 to hash their names and use the generated hashes to determine the nodes where the chunks will be stored.

### Scheduling the map tasks
Like the MapReduce framework, the number of mappers is equivalent to the number of chunks associated with the dataset. 
To ensure data locality for the map tasks, The framework uses dataset name to identifies the locations of its chunks in a deterministic way and launches the map tasks on nodes that host these data chunks. There is no need to contact the Master of NameNode to retrieve the locations of the files as the case of Hadoop. Bolt assigns the same chunks names to the map tasks so that they will be launched on DHT nodes that are responsible for the hashes of the assigned chunks.


### Scheduling the reduce tasks
Like MapReduce framework, Bolt requires that the developer identidies the number of reduce tasks.
Bolt aims to balance the workload of the reduce tasks by relying on novel mechanism to generate keys for reduce tasks in a deterministic way. The mechanism uses the jobID to create keys for the reduce taks, and the client hashes the generated keys to assign the reduce tasks to distributed system with high probability of balancing distribution. 

<p align="center">
<img width="500" alt="Reducer Keys" src="https://user-images.githubusercontent.com/40745827/87099935-49afb400-c208-11ea-92b2-daf11ac45d98.png">
</p>

Below is an example that illustrates the generation of the hashed keys to reduce tasks of 2 jobs (jobIDs 10 and 21) each has 4 reducers.  The identifiers of the jobs are 21 and 10. As we can see in the Figure, the initial keys, {0,16,32,48}, are the same for both jobs. 
<p align="center">
<img width="400" alt="Reducer Keys Generation" src="https://user-images.githubusercontent.com/40745827/87203183-4a575180-c2bf-11ea-8c68-f19e2194799b.png">
</p>

## Features
- Balancing the data and workload distribution among the available machines. 
- Ensuring data locality of the map tasks.
- Allowing the execution of MapReduce jobs in a structured P2P distributed system. 
- Distributing and locating data in a dynamic distributed environment. 
- Decentralized scheduling of MapReduce tasks. 
- Gaining a balanced workload without global knowledge about the system. 
- Providing scalability that allows the framework to execute more tasks as extra nodes are added to the system. 
- Considering a heterogeneous environment by assigning multiple identifiers to powerful nodes on the keyspace ring. In a such case, the powerful nodes will be 
  responsible for more workload than the others.
- Minimizing garbage collection overhead is achieved by reusing the same objects instead of creating new ones for new data.


## SYSTEM ARCHITECTURE
The architecture of Bolt allows concurrent executions of MapReduce tasks on a large structured P2P distributed system. Bolt is a framework composed of two main components, *client* and *resource manager*, for data staging and orchestrating of MapReduce tasks (see Figure below). 

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


### Resource Manager Components
The resource manager has 3 components:
- **Task Scheduler** queues and launches the received tasks in the same execution order of jobs on each node to minimize the job’s completion time. The scheduler aimes the execute tasks so that jobs will completed based on their submissio order.
- **Executor** manages a thread pool to facil- itate parallel tasks execution by utilizing the available cores and execution pipelines. Each executor can execute a fixed number of tasks concurrently without distinguishing between task’s types, map or reduce. The user can configure the thread pool size as proportion of the available cores on each machine.
- **Job Tasks Manager (JTM)** is created for each job that has one or more tasks running on the node. The JTM manages the execution of tasks that belong to the same job and are assigned to the same node. The resource manager is responsible for the JTMs creation and the communications between local and remote JTMs that belong to the same job. The JTM provides a transparent way to execute and track all the tasks of the same job. It provides all services needed for task’s execution such as supporting generic data types, delivering the input data to map tasks, pushing intermediate outputs to the reduce tasks, and locally storing the final outputs.
JTM is able to locate all running reducers for map tasks it manages and send the intermediate results to them. 

<p align="center">
<img width="500" alt="Resource Manager" src="https://user-images.githubusercontent.com/40745827/87204740-d0c16280-c2c2-11ea-82df-d8a5a3932ea2.png">
</p>

## Execution Path of MapReduce Tasks
The resource manager creates a JTM for each job the manager receives one or more of its tasks. The communications between local and remote JTMs that belong to the same job are done via the resource manager.
<p align="center">
<img width="500" alt="Execution Path" src="https://user-images.githubusercontent.com/40745827/87205749-5fcf7a00-c2c5-11ea-9367-efc2580e393c.png">
</p>


## Evaluation
For thee evaluation, we used a dataset of size 300 GB and a 77-node cluster with 47 HP DL160 servers (Xeon E5620 CPU, 12 GB of RAM) and 30 HP DL320 servers (Xeon E3-1220 V2 CPU, 8 GB of RAM).

### Word Count Job
Hadoop and Bolt gave been used to execute word count job with different number of reducers a number of times simultaneously. In the Figure below, **j** indices to number of concurrent running jobs and **r** indices to the number of reducers each job has.
<p align="center">
<img width="500" alt="WordCount Job" src="https://user-images.githubusercontent.com/40745827/87206722-ae7e1380-c2c7-11ea-9c44-34429b5d5fa9.png">
</p>
