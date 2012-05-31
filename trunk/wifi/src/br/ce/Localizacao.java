package br.ce;

public class Localizacao {
	private Integer pk;
	private String rede;
	private Integer sinal;
	private String localizacao;
	public Integer getPk() {
		return pk;
	}
	public void setPk(Integer pk) {
		this.pk = pk;
	}
	public String getRede() {
		return rede;
	}
	public void setRede(String rede) {
		this.rede = rede;
	}
	public Integer getSinal() {
		return sinal;
	}
	public void setSinal(Integer sinal) {
		this.sinal = sinal;
	}
	public String getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
}
