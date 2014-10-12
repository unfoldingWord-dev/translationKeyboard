class DashboardController < ApplicationController
  before_action :authenticate_user!
  def index
    @recently_added_keyboards = KeyboardVariant.all.order(updated_at: :desc).take(10)
  end
end
