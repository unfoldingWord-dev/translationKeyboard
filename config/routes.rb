Rails.application.routes.draw do

  apipie

  get 'language/index'

  get 'dashboard/index'

  devise_for :users
  get 'keyboard/index'
  get 'language/:iso_language', to: 'language#index', as: :language
  get 'region/:iso_region', to: 'region#index', as: :region
  get 'keyboard/variant/:keyboard_variant_id', to: 'keyboard#variant', as: :keyboard_variant
  match 'keyboard/variant/:keyboard_variant_id', to: 'keyboard#variant_destroy', :via => :delete

  get 'keyboard_convertor/convert'

  get 'characters/new'

  get 'characters/new_block'
  get 'language/get_reg'
  resources :keyboard do
  get :autocomplete_keyboard_languages_lc, :on => :collection
  get :autocomplete_keyboard_languages_lc_ln, :on => :collection
  end

 # get 'keyboard/addKey'
  #get 'keyboard/removeKey'
  #get 'post/:id' => 'posts#show'
  match '/add_key' => 'keyboard#add_row_key', :via => [:post]
  match '/remove_key' => 'keyboard#remove_row_key', :via => [:post]
  match '/view_modal' => 'keyboard#key_edit', :via => [:post]
  match '/load_character' => 'keyboard#load_char', :via => [:post]
  match '/import_lang_region' => 'keyboard#import_lang_region', :via => [:get]
  match '/save_new_position' => 'keyboard#update_position', :via => [:post]
  match '/save_region_name' => 'language#update_region_name', :via => [:post]
  match '/save_unicode_url' => 'language#update_unicode_url', :via => [:post]
  match '/get_reg_name' => 'language#get_reg_name', :via => [:post]
  match '/update_keyboard_name' => 'keyboard#update_keyboard_name', :via => [:post]
  match '/load_all_keyboard' => 'keyboard#load_all_keyboard', :via => [:get]
  # The priority is based upon order of creation: first created -> highest priority.
  # See how all your routes lay out with "rake routes".

  # You can have the root of your site routed with "root"
  root 'dashboard#index'

  resources :key_position
  resources :keyboard

  namespace :api do
    namespace :v1 do
      resources :keyboard
    end
    namespace :v2 do
      resources :keyboard
    end
    namespace :v3 do
      resources :keyboard
    end
  end

  # Example of regular route:
  #   get 'products/:id' => 'catalog#view'

  # Example of named route that can be invoked with purchase_url(id: product.id)
  #   get 'products/:id/purchase' => 'catalog#purchase', as: :purchase

  # Example resource route (maps HTTP verbs to controller actions automatically):
  #   resources :products

  # Example resource route with options:
  #   resources :products do
  #     member do
  #       get 'short'
  #       post 'toggle'
  #     end
  #
  #     collection do
  #       get 'sold'
  #     end
  #   end

  # Example resource route with sub-resources:
  #   resources :products do
  #     resources :comments, :sales
  #     resource :seller
  #   end

  # Example resource route with more complex sub-resources:
  #   resources :products do
  #     resources :comments
  #     resources :sales do
  #       get 'recent', on: :collection
  #     end
  #   end

  # Example resource route with concerns:
  #   concern :toggleable do
  #     post 'toggle'
  #   end
  #   resources :posts, concerns: :toggleable
  #   resources :photos, concerns: :toggleable

  # Example resource route within a namespace:
  #   namespace :admin do
  #     # Directs /admin/products/* to Admin::ProductsController
  #     # (app/controllers/admin/products_controller.rb)
  #     resources :products
  #   end
end
