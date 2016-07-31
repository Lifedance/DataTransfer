###配置文件说明
----------
执行容器的上下文是根据配置文件生成的，有数据源，类型装唤起，自定义的属性，还有最核心的，迁移任务配置；

* **数据源配置**

	
		<dataSources>
			<dataSource id="targetDB" dbType="mysql">
				<url>jdbc:mysql://192.168.31.108:3306/test</url>
				<username>xxx</username>
				<password>xxxx</password>
				<driverClass>com.mysql.jdbc.Driver</driverClass>
			</dataSource>
			<dataSource id="srcDB" dbType="mysql">
				<url>jdbc:mysql://192.168.31.108:3306/testA</url>
				<username>xxx</username>
				<password>xxxx</password>
				<driverClass>com.mysql.jdbc.Driver</driverClass>
			</dataSource>
		</dataSources>
	
	可以配多个数据源；每一个数据源都有一个自己的ID； 
	dbType: 数据库的类型： mySql, sqlServer, oracle ....

* **属性配置**

		<properties>
			<property name="poolSize" value="2"></property>
			<property name="pageSize" value="10000"></property>
			<property name="commitSize" value="5000"></property>
			<property name="fialRecordFilePath"  value="xxxxxxx" ></property> 
		</properties>	
	
	配置一些常用的属性，现在需要有个俩个配置：
	
	pageSize: 分页查询的时候，单页数据量的配置；
	
	fialRecordFilePath：记录迁移失败数据的文件的存放路径；
	
	**NOTE：** 如果有需要其他的配置，在进行自定义扩展的时候，可以按上面的格式进行配置，然后就可以从执行上下文中直接用getProperty(name) 来获取配置的值。
	
* **迁移任务配置**
	
	我们将需要迁移的目标数据库的表当做一个任务，而它数据可能来自多个表，甚至不同库的不同表，我们把他们分为下面种情况： 
	
	
	**A：同库单表**
	
		<table beanClass="com.xtonic.entity.User" table="user"  
			targetDataSourceRef="targetDB" id="hello"  			taskImplClass="com.xtonic.task.impl.InsertTask">
			<srcs>
				<src querySql="SELECT ID,USERNAME,AGE,INTERSTED,MEMO FROM SRCUSER"
					srcDataSourceRef="srcDB" keyColumn="id" />
			</srcs>
		</table> 
	
	
	**B：同库多表**
	
		
		<table beanClass="com.xtonic.entity.User" table="user"  
			targetDataSourceRef="targetDB" id="hello">
			<srcs>
				<src 
		querySql="select a.id,a.name as username,a.age,b.intersted,b.memo from testA.src3User a left join testA.src4User b on a.id = b.id" srcDataSourceRef="srcDB" keyColumn="id" />
			</srcs>
			<changes>
				<change columnName="age" targetType="java.lang.Integer"srcType="java.lang.String"  handlerRef="StringToInteger"/>
			</changes>
		</table>

	
	**C：多库多表**
	
		<table beanClass="com.xtonic.entity.User" table="user"  
			targetDataSourceRef="targetDB" id="userTable"  taskImplClass="com.xtonic.task.impl.MoreSrcsTransferTask">
			<srcs>
				<src querySql="select id,name as username,age from src3User "
					srcDataSourceRef="srcDB" keyColumn="id"  isMainSrc="true"/>
				<src querySql="select id,inttrested as intersted from src5user"
					srcDataSourceRef="targetDB" keyColumn="id" />
			</srcs>
			<changes>
				<change columnName="age" targetType="java.lang.Integer" srcType="java.lang.String"   handlerRef="StringToInteger"/>
			</changes>
		</table> 


上面是三种情况（应该是可以满足大多数的数据迁移需求）的配置列子；下面是对各个配置项的说明，及其注意点：

**table节点 表示的是一个迁移任务，其配置说明如下：**

beanClass: 目标表对应JAVA BEAN；
 
table: 目标表的表名 

targetDataSourceRef：目标表所在数据库对应的数据源的ID （DataSources 中配置的数据源）

taskImplClass: 配置迁移任务的实现类；如果没有配置，就采用默认的是实现类是：

		com.xtonic.task.impl.DefaultTransferTask；
		
		//在不涉及到多库多表的迁移任务的时候，采用这个默认的； 它满足的是单库单表，或单库多表的情景；
		
		//如果是多库多表的情况，就采用下面这个这个实现类：
		
		com.xtonic.task.impl.MoreSrcsTransferTask
		
*如果这俩个实现类都不能满足要求，就可以采用自己的扩展编写的实现类，在后续的**扩展说明**章节进行说明；*
		
	
**srcs节点： 表示数据源，配置的是如何获取要插入到目标表中的数据。可以有多个；**

srcDataSourceRef ： 数据源的ID； 在DataSource节点所配置的数据源的ID

keyColumn：关键字段，一般是主键，如果有多个字段组成的主键采用‘,’进行分割， keyColumn="id，username"
	
		在多库多表的情况下， 并且采用的是默认的com.xtonic.task.impl.MoreSrcsTransferTask的实现类的情况下： 
		几个数据源直接的keyColumn应该是一样额，是通过这个KeyColumn去关联不同数据库直接的数据的；
		
		比如： <srcs>
				<src ...  keyColumn="id,userNAME">
				<src .... keyColumn="id,name"
			  </srcs>
		这样的话，会采用  id = id , userName = name 这样的规则来匹对数据；
		
		

querySql: 从配置的数据库中获取源数据的SQL；这个SQL有几点要求：

		1：不允许采用 select * from table； 这种写法， 要采用select columnA, columnB.... from table		   这种写法；
		2：字段名必须与 beanClass属性配置的 目标表的JAVA BEAN的属性对应， 如果源数据表的字段与目标表的字段的名字有出入，请在querySQL中采用别名的写法， 字段的别名应该与JAVA BEAN的属性名对应；
		3： 字段的数量：多条SQL加起来的字段的总数，应该小于或等于JAVA BEAN的属性的个数；
		4： 如果配置了 KeyColumn属性，那么keyColumn字段应该在 querySQL里面存在；
		

**changes 节点： 是用来反映数据库结构变化的情况，进行再数据迁移的过程对对应的数据字段进行转换处理的处理器；**

columnName ：有变动的字段，该值应该是JAVA BEAN 里面对应的属性名； 其实也是目标表里面字段名；
targetType ：转换后的数据类型；
srcType： 转换前的数据类型；
handlerRef ： 转换处理的ID； （在转换器的配置）


* **类型转换器的配置**

		<typehandlers>
			<typehandler handlerid="StringToInteger" handlerClass="com.xtonic.type.impl.StringToIntegerHandler" />
		</typehandlers>


handlerid ： 转换器的ID；

handlerClass ： 转换器的是显现类； 该转换器，可能会涉及业务规则，或有一些特殊的情况，可以根据实际情况自己进行相关实现，并配置； **该实现类必须实现：com.xtonic.type.TypeChangeHander 接口；**


###扩展说明
-----------
在执行迁移任务的时候，主要三个核心的步骤，如下：

1. 获取数据，返回一个List集合，集合的类型为Mpa对象，KEY：为字段名，Value：属性值；
2. 将集合的一条条数据转换为对应的JAVE Bean； 中间包括了，类型转换处理；
3. 将一个个，JAVA BEAN 的数据，插入到对应的目标表中；

如果自带的任务实现类无法满足业务需求的话，可以自定义自己的任务实现类，并在XML中配置； 

***该任务实现类要继承 com.xtonic.task.AbstractTransferTask;***，并实现getDataFromSrc（） 返回的数据格式严格按照上述的规范。 

针对每一条数据进行特殊的处理，可以重写customProcessValues(List values, List<String> fieldNames)方法；

values:  一个JAVA BEAN存的数据的值。
fieldNames: JAVA BENA对应的字段；
values 与 fieldNames一一对应；

###运行
------
	import java.io.IOException;

	import com.xtonic.context.TransferContext;
	import com.xtonic.context.impl.TrnasferAppContext;

	public class Main {
		public static void main(String[] args) throws IOException, InterruptedException {
			//初始化XML文件，生成上下文
			TransferContext context = new TrnasferAppContext("TransferDefinitions.xml");
			//将上下文丢到容器里面进行运行；
 			Runcontainer containor = new Runcontainer(context);
			containor.excute();
		}
	}

		
