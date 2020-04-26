package backend.misc;

import backend.list.BaseList;
import backend.list.entity.EntityList;
import backend.list.tag.TagList;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import main.Root;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Comparator;

public class Project {
	private transient String projectName;
	private transient String projectFile;
	
	@SerializedName("a") private long msLastAccess;
	@SerializedName("s") private String directory;
	@SerializedName("e") private EntityList entityList;
	@SerializedName("t") private TagList tagList;
	
	public Project(String projectName, String directory) {
		this.projectName = projectName;
		this.projectFile = createProjectFilePath(projectName);
		this.directory = directory;
	}
	
	private transient static final Type typeToken = new TypeToken<Project>() {}.getType();
	public static Project readFromDisk(String projectFile) {
		Project project = GsonUtil.read(typeToken, projectFile);
		if (project != null) {
			project.projectName = FileUtil.getFileNameNoExtension(projectFile);
			project.projectFile = projectFile;
		}
		return project;
	}
	public boolean writeToDisk() {
		this.msLastAccess = System.currentTimeMillis();
		this.entityList = Root.ENTITYLIST;
		this.tagList = Root.TAGLIST;
		return GsonUtil.write(this, typeToken, projectFile);
	}
	
	public void updateProject(String projectNameNew, String directorySourceNew) {
		if (projectName.equals(projectNameNew)) {
			this.directory = directorySourceNew;
			this.writeToDisk();
		} else {
			String projectFileNew = createProjectFilePath(projectNameNew);
			if (!new File(projectFileNew).exists()) {
				FileUtil.moveFile(projectFile, projectFileNew);
				FileUtil.moveFile(FileUtil.getDirectoryCache(this.projectName), FileUtil.getDirectoryCache(projectNameNew));
				
				this.projectName = projectNameNew;
				this.projectFile = projectFileNew;
				this.directory = directorySourceNew;
			}
		}
	}
	
	public static Comparator<Project> getComparator() {
		return (o1, o2) -> (int) (o2.getMsLastAccess() - o1.getMsLastAccess());
	}
	
	public String getProjectName() {
		return projectName;
	}
	public String getProjectFile() {
		return projectFile;
	}
	
	public long getMsLastAccess() {
		return msLastAccess;
	}
	public String getDirectory() {
		return directory;
	}
	public EntityList getEntityList() {
		return entityList;
	}
	public TagList getTagList() {
		return tagList;
	}
	
	public static String createProjectFilePath(String projectName) {
		return FileUtil.getDirectoryProject() + File.separator + projectName + ".json";
	}
	
	private static transient Project current;
	public static Project getCurrent() {
		return current;
	}
	public static void setCurrent(Project project) {
		Project.current = project;
	}
	public static void setFirstAsCurrent() {
		BaseList<Project> projects = FileUtil.getProjects();
		projects.sort(Project.getComparator());
		setCurrent(projects.getFirstImpl());
	}
}
