package com.jenkinsjobs.model;

public class JobParameter {
	private String paramName;
	private String value;
	private String paramType;
	
	public JobParameter() {
		super();
	}
	public String getParamName() {
		return paramName;
	}
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getParamType() {
		return paramType;
	}
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((paramName == null) ? 0 : paramName.hashCode());
		result = prime * result + ((paramType == null) ? 0 : paramType.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobParameter other = (JobParameter) obj;
		if (paramName == null) {
			if (other.paramName != null)
				return false;
		} else if (!paramName.equals(other.paramName))
			return false;
		if (paramType == null) {
			if (other.paramType != null)
				return false;
		} else if (!paramType.equals(other.paramType))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "JobParameter [paramName=" + paramName + ", value=" + value + ", paramType=" + paramType + "]";
	}
	
	
}
