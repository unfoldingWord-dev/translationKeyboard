class LanguageController < ApplicationController
  before_action :authenticate_user!
  def index
    @language_obj = LanguageList::LanguageInfo.find(params[:iso_language])
    @keyboards_for_language_iso = Keyboard.where(iso_language: params[:iso_language])
  end
end
